package actors.frontend

import actors.developer.Mediator
import akka.actor._
import models.frontend._
import scala.concurrent.duration._
import akka.cluster.sharding.ClusterSharding

/**
 * Actor to connect a tool developer to the backend
 */

object DeveloperDummy {
  def props() = Props(new DeveloperDummy())

  private case object Register
}

class DeveloperDummy() extends Actor with ActorLogging {
  import DeveloperDummy._
  import context.dispatcher

  val userId = "modigen"

  val backend: ActorRef = ClusterSharding(context.system).shardRegion(Mediator.shardRegionName)

  val instance = ToolDeveloper(self, userId)
  val registerTask = context.system.scheduler.schedule(1.seconds, 10.seconds, self, Register)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case Register =>
      backend ! MessageEnvelope(userId, Connected(instance))
    case request: DeveloperRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: DeveloperResponse =>
      log.info("Message from backend [{}] ", response.toString)
  }
}
