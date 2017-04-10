package actors.frontend

import actors.developer.Mediator
import akka.actor._
import models.frontend._
import scala.concurrent.duration._
import akka.cluster.sharding.ClusterSharding

/**
 * Actor to connect a model user to the backend
 */

object UserFrontend {
  def props(out: ActorRef, backend: ActorRef, userId: String, model: String) = Props(new UserFrontend(out, backend, userId, model))

  private case object Register
}

class UserFrontend(out: ActorRef, backend: ActorRef, userId: String, model: String) extends Actor with ActorLogging {
  import UserFrontend._
  import context.dispatcher

  val instance = ModelUser(self, userId, model)
  val registerTask = context.system.scheduler.schedule(1.seconds, 30.seconds, self, Register)

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
    case request: UserRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: UserResponse =>
      out ! response
  }
}
