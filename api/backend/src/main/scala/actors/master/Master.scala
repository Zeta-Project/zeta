package actors.master

import java.util.concurrent.TimeUnit

import actors.worker.MasterWorkerProtocol
import actors.worker.MasterWorkerProtocol.CancelWork
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.persistence.PersistentActor

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Deadline
import scala.concurrent.duration.FiniteDuration

private sealed trait WorkerStatus

private case object Idle extends WorkerStatus

private case class Busy(workId: String, deadline: Deadline) extends WorkerStatus

private case class WorkerState(ref: ActorRef, status: WorkerStatus, deadline: Deadline)

private case object WorkerTimeoutTick

private case object CompletedWorkTick

object Master {
  def props(workerTimeout: FiniteDuration, sessionDuration: FiniteDuration): Props =
    Props(classOf[Master], workerTimeout, sessionDuration)
}

class Master(workerTimeout: FiniteDuration, sessionDuration: FiniteDuration) extends PersistentActor with ActorLogging {
  val numberOfWorkersToNotify = 4
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("Master", self)

  ClusterClientReceptionist(context.system).registerService(self)

  // persistenceId must include cluster role to support multiple masters
  override def persistenceId: String = Cluster(context.system).selfRoles.find(_.startsWith("backend-")) match {
    case Some(role) => role + "-master"
    case None => "master"
  }

  // workers state is not event sourced
  private var workers = Map[String, WorkerState]()

  // workState is event sourced
  private var workState = WorkState.empty()

  private val workerTimeoutTask = context.system.scheduler.schedule(workerTimeout / 2, workerTimeout / 2, self, WorkerTimeoutTick)
  private val completedWorkTask = context.system.scheduler.schedule(Duration(10, TimeUnit.SECONDS), Duration(10, TimeUnit.SECONDS), self, CompletedWorkTick)

  override def postStop() = {
    workerTimeoutTask.cancel()
    completedWorkTask.cancel()
  }

  override def receiveRecover: Receive = {
    case event: WorkDomainEvent => workState = workState.updated(event)
  }

  def workerDeathTimeout = Deadline.now + workerTimeout

  override def receiveCommand: Receive = {
    /**
     * Worker send a registration message
     */
    case MasterWorkerProtocol.RegisterWorker(workerId) => registerWorker(workerId)

    /**
     * Worker is asking for work
     */
    case MasterWorkerProtocol.WorkerRequestsWork(workerId) => sendWork(workerId)

    /**
     * Worker successful executed work
     */
    case MasterWorkerProtocol.WorkIsDone(workerId, workId, result) => processWorkResult(workerId, workId, result)

    /**
     * Worker executed work and work failed.
     */
    case MasterWorkerProtocol.WorkFailed(workerId, workId) => processWorkFailed(workerId, workId)

    /**
     * Developer sends ack that he received the result of the completed work
     */
    case MasterWorkerProtocol.DeveloperReceivedCompletedWork(workId) => confirmWorkResult(workId)

    /**
     * New work was send by a developer.
     */
    case work: MasterWorkerProtocol.Work => processWork(work)

    /**
     * Work should be canceled.
     */
    case cancelWork: CancelWork => processCancelWork(cancelWork)
    case WorkerTimeoutTick => processWorkerTimeout()

    /**
     * Check for completed work from which no ack was received by the developer (which started the work).
     */
    case CompletedWorkTick => processWorkCompleted()
  }

  private def registerWorker(workerId: String) = {
    if (workers.contains(workerId)) {
      workers += (workerId -> workers(workerId).copy(ref = sender(), deadline = workerDeathTimeout))
    } else {
      workers += (workerId -> WorkerState(sender(), Idle, workerDeathTimeout))
      if (workState.hasWork) {
        sender() ! MasterWorkerProtocol.WorkIsReady
      }
    }
  }

  private def sendWork(workerId: String) = {
    if (workState.hasWork) {
      workers.get(workerId) match {
        case Some(worker@WorkerState(_, Idle, _)) =>
          val workerRef = sender()
          val work = workState.nextWork
          persist(WorkStarted(work.id)) { event =>
            workState = workState.updated(event)
            workers += (workerId -> worker.copy(status = Busy(work.id, Deadline.now + workerTimeout)))
            workerRef ! work
          }
        case _ =>
      }
    }
  }

  private def processWorkResult(workerId: String, workId: String, result: Int) = {
    // send a ack to the worker to tell the worker that the message was received
    sender() ! MasterWorkerProtocol.Ack(workId)
    // set the worker back to idle state
    changeWorkerToIdle(workerId)
    // if the work is not persisted as "done", we need to persist this state
    if (!workState.isDone(workId)) {
      persist(WorkCompleted(workId, result)) { event =>
        workState = workState.updated(event)
        workState.completedWorkById(workId) match {
          case Some(completed) =>
            log.info("Sending work {} as completed to the work creator {}", workerId, completed.work.owner)
            mediator ! DistributedPubSubMediator.Publish(completed.work.owner.toString, MasterWorkerProtocol.MasterCompletedWork(workId, result))
          case None =>
            // should not happen
            log.error("Completed work {} was not available as expected", workId)
        }
      }
    }
  }

  private def processWorkFailed(workerId: String, workId: String) = {
    // send a ack to the worker to tell the worker that the message was received
    sender() ! MasterWorkerProtocol.Ack(workId)
    // set the worker back to idle state
    changeWorkerToIdle(workerId)

    if (workState.isInProgress(workId)) {
      log.info("Work {} failed by worker {}", workId, workerId)
      changeWorkerToIdle(workerId)
      persist(WorkerFailed(workId)) { event =>
        workState = workState.updated(event)
        notifyWorkers()
      }
    }
  }

  private def confirmWorkResult(workId: String) = {
    log.info("Master received ack that work completion was received {}", workId)
    persist(WorkCompletionReceived(workId)) { event =>
      workState = workState.updated(event)
    }
  }

  private def processWork(work: MasterWorkerProtocol.Work) = {
    if (workState.isAccepted(work.id)) {
      sender() ! MasterWorkerProtocol.MasterAcceptedWork(work.id)
    } else {
      persist(WorkAccepted(work)) { event =>
        sender() ! MasterWorkerProtocol.MasterAcceptedWork(work.id)
        workState = workState.updated(event)
        notifyWorkers()
      }
    }
  }

  private def processCancelWork(cancelWork: CancelWork) = {
    log.info("Cancel work: {}", cancelWork.id)
    val reply = sender()

    // check if a worker is working on the workId
    val worker = workers.find {
      case (workerId, state) => state.status match {
        case Idle => false
        case Busy(workId, deadline) => workId == cancelWork.id
      }
    }
    // if a worker is working send the worker a cancel. otherwise the work is already done
    worker match {
      case Some(x) => x._2.ref ! cancelWork
      case None => reply ! MasterWorkerProtocol.MasterCompletedWork(cancelWork.id, 3)
    }
  }

  private def processWorkerTimeout() = {
    /**
     * A busy worker actor reached the timeout
     */
    for {(workerId, s@WorkerState(_, Busy(workId, timeout), _)) <- workers} {
      if (timeout.isOverdue) {
        log.info("Work timed out: {}", workId)
        workers -= workerId
        persist(WorkerTimedOut(workId)) { event =>
          workState = workState.updated(event)
          notifyWorkers()
        }
      }
    }
    /**
     * A idle worker actor reached the timeout
     */
    for {(workerId, s@WorkerState(_, Idle, timeout)) <- workers} {
      if (timeout.isOverdue) {
        log.info("Worker timed out and removed from the system: {}", workerId)
        workers -= workerId
      }
    }
  }

  private def processWorkCompleted() = {
    workState.completedWorkList foreach { completed =>
      log.info(s"Re-send work completed : '${completed.work.job}' to ${completed.work.owner}")
      mediator ! DistributedPubSubMediator.Publish(completed.work.owner.toString, MasterWorkerProtocol.MasterCompletedWork(completed.work.id, completed.result))
    }
  }

  /**
   * Get idle workers
   *
   * @return A map with (workerId -> WorkerState) of IDLE workers
   */
  private def idleWorkers() = {
    workers.filter {
      case (workerId, workerState) => workerState.status match {
        case Idle => true
        case Busy(workId, deadline) => false
      }
    }
  }

  /**
   * Send a few idle workers that work is available
   */
  private def notifyWorkers(): Unit = {
    if (workState.hasWork) {
      idleWorkers
        .take(numberOfWorkersToNotify)
        .foreach {
          case (_, WorkerState(ref, Idle, _)) => ref ! MasterWorkerProtocol.WorkIsReady
        }
    }
  }

  private def changeWorkerToIdle(workerId: String): Unit = {
    workers.get(workerId) match {
      case Some(worker@WorkerState(_, Busy(_, _), _)) =>
        workers += (workerId -> worker.copy(status = Idle))
      case _ =>
      // ok, might happen after standby recovery, worker state is not persisted
    }
  }
}
