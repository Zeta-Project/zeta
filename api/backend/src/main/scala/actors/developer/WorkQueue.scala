package actors.developer

import actors.developer.WorkState._
import actors.worker.MasterWorkerProtocol._
import akka.actor.{ ActorLogging, Props }
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }
import akka.persistence.PersistentActor
import models.document._
import scala.concurrent.duration._
import models.frontend.{ CancelWorkByUser, JobInfo, JobInfoList }
import models.worker._

object WorkQueue {
  def props(developer: String) = Props(new WorkQueue(developer: String))

  case class JobCannotBeEnqueued(job: Job, reason: String)
  case object GetJobInfoList
  private case object CheckTick
  private case object NextWork
}

class WorkQueue(developer: String) extends PersistentActor with ActorLogging {
  import WorkQueue._
  import DistributedPubSubMediator.Publish

  val PENDING_JOB = "pending"
  val WAITING_JOB = "waiting"
  val RUNNING_JOB = "running"

  val reply = context.parent
  val master = DistributedPubSub(context.system).mediator

  private var jobSettings = JobSettings.default()

  import context.dispatcher

  // workState is event sourced
  private var workState = WorkState.empty()

  // trigger task to check if work was accepted from the master
  val workAcceptedTask = context.system.scheduler.schedule(10.seconds, 10.seconds, self, CheckTick)

  override def postStop(): Unit = workAcceptedTask.cancel()

  // tasks can be enqueued regardless of the running tasks.
  // manual filter and generator execution can only be enqueued if not already
  // the same generator or filter is running
  def jobCanBeEnqueued(job: Job): Boolean = job match {
    case run: RunGeneratorFromGeneratorJob => parentExist(run)
    case run: RunFilterManually => equalJobNotExist(run)
    case run: RunGeneratorManually => equalJobNotExist(run)
    case run: RunEventDrivenTask => true
    case run: RunTimedTask => true
    case run: CreateMetaModelReleaseJob => equalJobNotExist(run)
    case run: CreateGeneratorJob => equalJobNotExist(run)
    case run: RerunFilterJob => equalJobNotExist(run)
    case run: RunBondedTask => equalJobNotExist(run)
  }

  // check if a child generator can be started. Can only be started if a parent is already running
  def parentExist(job: RunGeneratorFromGeneratorJob) = workState.workInProgress.exists(_._1 == job.parentId)

  // check if there is a not a equal job running or enqueued.
  def equalJobNotExist(starting: Job): Boolean = jobInfoList((work) => work.job.equals(starting)).jobs.isEmpty

  // build a JobInfoList from all jobs
  def jobInfoList(filter: Work => Boolean): JobInfoList = {
    val maxToSend = 100
    val running = workState.workInProgress.take(maxToSend).values.filter(filter).map(work => JobInfo(work.id, work.job, RUNNING_JOB)).toList
    val takePending = maxToSend - running.length
    val pending = workState.pendingWork.take(takePending).filter(filter).map(work => JobInfo(work.id, work.job, PENDING_JOB)).toList
    val takeWait = maxToSend - pending.length - running.length
    val waiting = workState.workInWait.take(takeWait).values.filter(filter).map(work => JobInfo(work.id, work.job, WAITING_JOB)).toList
    JobInfoList(
      jobs = pending ::: waiting ::: running,
      running = workState.workInProgress.size,
      pending = workState.pendingWork.size,
      waiting = workState.workInWait.size,
      canceled = workState.workCanceled.size
    )
  }

  def publishEvent(event: Event) = {
    log.info("Publish event : {}", event.toString)
    sendJobInfoList
    context.parent ! event
  }

  def sendJobInfoList = context.parent ! jobInfoList((work) => true)

  override def receiveRecover: Receive = {
    case event: Event => workState = workState.updated(event)
  }

  override def receiveCommand: Receive = {
    // Settings changed
    case Changed(settings: Settings, Created) => jobSettings = settings.jobSettings.copy()
    case Changed(settings: Settings, Updated) => jobSettings = settings.jobSettings.copy()

    // received a job. Enqueue the job and send it to the master
    case job: Job =>
      log.info(s"Received job ${job}")
      if (jobSettings.maxPending < workState.pendingWork.length) {
        val reason = s"Received job ${job} cannot be enqueued because of max pending work!"
        reply ! JobCannotBeEnqueued(job, reason)
      } else if (jobCanBeEnqueued(job)) {
        val work = Work(job = job, owner = developer, dockerSettings = jobSettings.docker.copy())
        persist(WorkEnqueued(work)) { event =>
          workState = workState.updated(event)
          self ! NextWork
          publishEvent(event)
        }
      } else {
        val reason = s"Received job ${job} cannot be enqueued!"
        reply ! JobCannotBeEnqueued(job, reason)
      }
    case NextWork =>
      if (workState.hasWork && workState.numberOfRunning < jobSettings.maxRunning) {
        val work = workState.nextWork
        log.info(s"Send job to master ${work.job}")
        persist(WorkSendToMaster(work)) { event =>
          workState = workState.updated(event)
          sendToMaster(work)
          publishEvent(event)
        }
      }
    // Cancel a job
    case CancelWorkByUser(workId) =>
      workState.getWorkFromAnyState(workId) foreach { work =>
        persist(WorkCanceled(work)) { event =>
          workState = workState.updated(event)
          sendToMaster(CancelWork(workId))
          publishEvent(event)
        }
      }

    // Master accepted work, save this state
    case MasterAcceptedWork(workId) =>
      log.info(s"Master accepted work ${workId}")
      workState.getWorkInWait(workId) match {
        case Some(work) => {
          persist(WorkAcceptedByMaster(work)) { event =>
            workState = workState.updated(event)
            publishEvent(event)
          }
        }
        case None => log.info("accepted received twice for {}", workId)
      }
    // Master send work is done, save this state
    case MasterCompletedWork(workId, result) =>
      log.info(s"Master completed work ${workId}")
      // check if work was in progress
      workState.getWorkInProgress(workId) foreach { work =>
        log.info(s"Work was in progress and ended now ${workId}")
        persist(WorkCompleted(work, result)) { event =>
          workState = workState.updated(event)
          self ! NextWork
          publishEvent(event)
        }
      }
      // otherwise check if work was canceled
      workState.getCanceledWork(workId) foreach { work =>
        log.info(s"Work was canceled ${workId}")
        persist(WorkCompleted(work, result)) { event =>
          workState = workState.updated(event)
          self ! NextWork
          publishEvent(event)
        }
      }

      // answer the master that we received it's message.
      sender() ! DeveloperReceivedCompletedWork(workId)
    // check if there is any work which is not yet accepted by the master
    case CheckTick =>
      workState.listWorkInWait.foreach(work => {
        log.info("ask for waiting work {}", work._1)
        sendToMaster(work)
      })
      workState.listCanceledWork.foreach(workId => {
        log.info("ask for canceled work {}", workId)
        sendToMaster(CancelWork(workId))
      })
    case GetJobInfoList =>
      sendJobInfoList
  }

  private def sendToMaster(message: Any) = master ! Publish("Master", message)

  override def persistenceId: String = s"worker-${developer}"
}
