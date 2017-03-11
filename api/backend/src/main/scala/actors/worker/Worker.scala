package actors.worker

import java.util.UUID

import actors.worker.MasterWorkerProtocol.{ CancelWork, Work }
import actors.worker.Worker.WorkTimeout
import akka.actor.{ Actor, ActorInitializationException, ActorLogging, ActorRef, DeathPactException, OneForOneStrategy, Props, ReceiveTimeout, Terminated }
import akka.actor.SupervisorStrategy.{ Restart, Stop }
import akka.cluster.pubsub.{ DistributedPubSub, DistributedPubSubMediator }

import scala.concurrent.duration._

object Worker {
  def props(executor: ActorRef, registerInterval: FiniteDuration = 10.seconds, workTimeout: FiniteDuration): Props =
    Props(classOf[Worker], executor, registerInterval, workTimeout)

  case class WorkComplete(result: Int)

  object WorkTimeout
}

class Worker(executor: ActorRef, registerInterval: FiniteDuration, workTimeout: FiniteDuration) extends Actor with ActorLogging {
  import DistributedPubSubMediator.Publish
  val mediator = DistributedPubSub(context.system).mediator
  val workerId = UUID.randomUUID().toString

  import context.dispatcher
  val registerTask = context.system.scheduler.schedule(10.seconds, registerInterval, mediator,
    Publish("Master", MasterWorkerProtocol.RegisterWorker(workerId)))

  val workTimeoutTask = context.system.scheduler.schedule(10.seconds, workTimeout / 4, self, WorkTimeout)

  val workExecutor = context.watch(executor)

  var currentWork: Work = null

  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException => Stop
    case _: Exception if currentWork != null =>
      sendToMaster(MasterWorkerProtocol.WorkFailed(workerId, currentWork.id))
      context.become(idle)
      Restart
  }

  override def postStop(): Unit = registerTask.cancel()

  def receive = idle

  def idle: Receive = {
    case MasterWorkerProtocol.WorkIsReady =>
      log.info("Got message WorkIsReady")
      sendToMaster(MasterWorkerProtocol.WorkerRequestsWork(workerId))

    case work: Work =>
      currentWork = work
      workExecutor ! work
      context.become(working(Deadline.now + workTimeout))

    case cancelWork: CancelWork =>
      log.warning("No work is running to cancel!")

    case _ =>
  }

  def working(deadline: Deadline): Receive = {
    case cancelWork: CancelWork => {
      if (cancelWork.id == currentWork.id) {
        log.info("Send Cancel work {} to work executor", cancelWork.id)
        workExecutor ! cancelWork
      } else {
        log.warning("Received Cancel work {} but this work is not executed by this worker", cancelWork.id)
      }
    }
    case Worker.WorkComplete(result) =>
      log.info("Work is complete. Result {}.", result)
      sendToMaster(MasterWorkerProtocol.WorkIsDone(workerId, currentWork.id, result))
      context.setReceiveTimeout(5.seconds)
      context.become(waitForWorkIsDoneAck(result))
    case msg: MasterWorkerProtocol.WorkerStreamedMessage =>
      mediator ! Publish(currentWork.owner, msg)
    case _: Work =>
      log.error("Master told working worker to execute other job.")
    case WorkTimeout => if (deadline.isOverdue()) {
      log.info("Cancel work due to reached deadline")
      workExecutor ! CancelWork(currentWork.id)
    }
  }

  def waitForWorkIsDoneAck(result: Int): Receive = {
    case MasterWorkerProtocol.Ack(id) if id == currentWork.id =>
      log.info("Ack from Master received")
      sendToMaster(MasterWorkerProtocol.WorkerRequestsWork(workerId))
      context.setReceiveTimeout(Duration.Undefined)
      context.become(idle)
      currentWork = null
    case ReceiveTimeout =>
      log.info("No ack from master. Retry now.")
      sendToMaster(MasterWorkerProtocol.WorkIsDone(workerId, currentWork.id, result))
    case CancelWork =>
      log.warning("No work is running to cancel!")
    case _ =>
  }

  override def unhandled(message: Any): Unit = message match {
    case Terminated(`workExecutor`) => context.stop(self)
    case MasterWorkerProtocol.WorkIsReady =>
    case _ => super.unhandled(message)
  }

  def sendToMaster(msg: Any): Unit = {
    mediator ! Publish("Master", msg)
  }
}