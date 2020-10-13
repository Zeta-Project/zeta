package de.htwg.zeta.generatorControl.actors.worker

import java.util.UUID
import java.util.concurrent.TimeUnit

import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.CancelWork
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work
import akka.actor.Actor
import akka.actor.ActorInitializationException
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.DeathPactException
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.actor.Terminated
import akka.actor.SupervisorStrategy.Restart
import akka.actor.SupervisorStrategy.Stop
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Deadline
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

object WorkTimeout
case class WorkComplete(result: Int)

object Worker {
  def props(executor: ActorRef, registerInterval: FiniteDuration = Duration(10, TimeUnit.SECONDS), workTimeout: FiniteDuration): Props =
    Props(classOf[Worker], executor, registerInterval, workTimeout)
}

class Worker(executor: ActorRef, registerInterval: FiniteDuration, workTimeout: FiniteDuration) extends Actor with ActorLogging {
  private val mediator = DistributedPubSub(context.system).mediator
  private val workerId = UUID.randomUUID().toString

  private val registerTask = context.system.scheduler.scheduleAtFixedRate(Duration(10, TimeUnit.SECONDS), registerInterval, mediator,
    Publish("Master", MasterWorkerProtocol.RegisterWorker(workerId)))

  private val workTimeoutTask = context.system.scheduler.scheduleAtFixedRate(Duration(10, TimeUnit.SECONDS), workTimeout / 4, self, WorkTimeout)

  private val workExecutor = context.watch(executor)

  private var currentWork: Work = null // scalastyle:ignore null

  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException => Stop
    case _: Exception if currentWork != null => // scalastyle:ignore null
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
    case WorkComplete(result) =>
      log.info("Work is complete. Result {}.", result)
      sendToMaster(MasterWorkerProtocol.WorkIsDone(workerId, currentWork.id, result))
      context.setReceiveTimeout(Duration(5, TimeUnit.SECONDS))
      context.become(waitForWorkIsDoneAck(result))
    case msg: MasterWorkerProtocol.WorkerStreamedMessage =>
      mediator ! Publish(currentWork.owner.toString, msg)
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
      currentWork = null // scalastyle:ignore null
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
