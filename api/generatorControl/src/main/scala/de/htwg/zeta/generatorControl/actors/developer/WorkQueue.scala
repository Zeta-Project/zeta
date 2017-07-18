package de.htwg.zeta.generatorControl.actors.developer

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.persistence.PersistentActor
import de.htwg.zeta.common.models.document.Changed
import de.htwg.zeta.common.models.document.Created
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.document.Updated
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.frontend.CancelWorkByUser
import de.htwg.zeta.common.models.frontend.JobInfo
import de.htwg.zeta.common.models.frontend.JobInfoList
import de.htwg.zeta.common.models.worker.CreateGeneratorJob
import de.htwg.zeta.common.models.worker.CreateMetaModelReleaseJob
import de.htwg.zeta.common.models.worker.Job
import de.htwg.zeta.common.models.worker.RerunFilterJob
import de.htwg.zeta.common.models.worker.RunBondedTask
import de.htwg.zeta.common.models.worker.RunEventDrivenTask
import de.htwg.zeta.common.models.worker.RunFilterManually
import de.htwg.zeta.common.models.worker.RunGeneratorFromGeneratorJob
import de.htwg.zeta.common.models.worker.RunGeneratorManually
import de.htwg.zeta.common.models.worker.RunTimedTask
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.CancelWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.DeveloperReceivedCompletedWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.MasterAcceptedWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.MasterCompletedWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work

case class JobCannotBeEnqueued(job: Job, reason: String)
case object GetJobInfoList
private case object CheckTick
private case object NextWork

object WorkQueue {
  def props(developer: UUID): Props = Props(new WorkQueue(developer))
}

class WorkQueue(developer: UUID) extends PersistentActor with ActorLogging {
  private val PENDING_JOB = "pending"
  private val WAITING_JOB = "waiting"
  private val RUNNING_JOB = "running"

  private val reply: ActorRef = context.parent
  private val master: ActorRef = DistributedPubSub(context.system).mediator

  private var jobSettings: JobSettings = JobSettings.default()

  // workState is event sourced
  private var workState: WorkState = WorkState.empty()

  // trigger task to check if work was accepted from the master
  private val duration = Duration(10, TimeUnit.SECONDS)
  private val workAcceptedTask: Cancellable = context.system.scheduler.schedule(duration, duration, self, CheckTick)

  override def postStop(): Unit = workAcceptedTask.cancel()

  // tasks can be enqueued regardless of the running tasks.
  // manual filter and generator execution can only be enqueued if not already
  // the same generator or filter is running
  private def jobCanBeEnqueued(job: Job): Boolean = job match {
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
  private def parentExist(job: RunGeneratorFromGeneratorJob): Boolean = workState.workInProgress.exists(_._1 == job.parentId)

  // check if there is a not a equal job running or enqueued.
  private def equalJobNotExist(starting: Job): Boolean = jobInfoList((work) => work.job.equals(starting)).jobs.isEmpty

  // build a JobInfoList from all jobs
  private def jobInfoList(filter: Work => Boolean): JobInfoList = {
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

  private def publishEvent(event: Event): Unit = {
    log.info("Publish event : {}", event.toString)
    sendJobInfoList()
    context.parent ! event
  }

  private def sendJobInfoList(): Unit = context.parent ! jobInfoList((work) => true)

  override def receiveRecover: Receive = {
    case event: Event => workState = workState.updated(event)
  }

  override def receiveCommand: Receive = {
    // Settings changed
    case Changed(settings: Settings, Created) => jobSettings = settings.jobSettings.copy()
    case Changed(settings: Settings, Updated) => jobSettings = settings.jobSettings.copy()
    // received a job. Enqueue the job and send it to the master
    case job: Job => processJob(job)
    case NextWork => processNextWork()
    // Cancel a job
    case CancelWorkByUser(workId) => cancelJob(workId)
    // Master accepted work, save this state
    case MasterAcceptedWork(workId) => acceptWork(workId)
    // Master send work is done, save this state
    case MasterCompletedWork(workId, result) => signalWorkDone(workId, result)
    // check if there is any work which is not yet accepted by the master
    case CheckTick => processCheck()
    case GetJobInfoList => sendJobInfoList()
  }

  private def processJob(job: Job): Unit = {
    log.info(s"Received job $job")
    if (jobSettings.maxPending < workState.pendingWork.length) {
      val reason = s"Received job $job cannot be enqueued because of max pending work!"
      reply ! JobCannotBeEnqueued(job, reason)
    } else if (jobCanBeEnqueued(job)) {
      val work = createWork(job)
      persist(WorkEnqueued(work)) { event =>
        workState = workState.updated(event)
        self ! NextWork
        publishEvent(event)
      }
    } else {
      val reason = s"Received job $job cannot be enqueued!"
      reply ! JobCannotBeEnqueued(job, reason)
    }
  }

  private def createWork(job: Job): Work = {
    Work(
      job = job,
      owner = developer,
      dockerSettings = jobSettings.docker.copy()
    )
  }

  private def processNextWork(): Unit = {
    if (workState.hasWork && workState.numberOfRunning < jobSettings.maxRunning) {
      val work = workState.nextWork
      log.info(s"Send job to master ${work.job}")
      persist(WorkSendToMaster(work)) { event =>
        workState = workState.updated(event)
        sendToMaster(work)
        publishEvent(event)
      }
    }
  }

  private def cancelJob(workId: String): Unit = {
    workState.getWorkFromAnyState(workId) foreach { work =>
      persist(WorkCanceled(work)) { event =>
        workState = workState.updated(event)
        sendToMaster(CancelWork(workId))
        publishEvent(event)
      }
    }
  }

  private def acceptWork(workId: String): Unit = {
    log.info(s"Master accepted work $workId")
    workState.getWorkInWait(workId) match {
      case Some(work) =>
        persist(WorkAcceptedByMaster(work)) { event =>
          workState = workState.updated(event)
          publishEvent(event)
        }
      case None => log.info("accepted received twice for {}", workId)
    }
  }

  private def signalWorkDone(workId: String, result: Int): Unit = {
    log.info(s"Master completed work $workId")
    // check if work was in progress
    workState.getWorkInProgress(workId) foreach { work =>
      log.info(s"Work was in progress and ended now $workId")
      persist(WorkCompleted(work, result)) { event =>
        workState = workState.updated(event)
        self ! NextWork
        publishEvent(event)
      }
    }
    // otherwise check if work was canceled
    workState.getCanceledWork(workId) foreach { work =>
      log.info(s"Work was canceled $workId")
      persist(WorkCompleted(work, result)) { event =>
        workState = workState.updated(event)
        self ! NextWork
        publishEvent(event)
      }
    }

    // answer the master that we received it's message.
    sender() ! DeveloperReceivedCompletedWork(workId)
  }

  private def processCheck(): Unit = {
    workState.listWorkInWait.foreach(work => {
      log.info("ask for waiting work {}", work._1)
      sendToMaster(work)
    })
    workState.listCanceledWork.foreach(workId => {
      log.info("ask for canceled work {}", workId)
      sendToMaster(CancelWork(workId))
    })
  }

  private def sendToMaster(message: Any): Unit = master ! Publish("Master", message)

  override def persistenceId: String = s"worker-$developer"
}
