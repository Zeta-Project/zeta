package actors.frontend

import akka.actor._
import models.frontend._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

private case object RegisterUserFrontend

/**
 * Actor to connect a model user to the backend
 */

object UserFrontend {
  def props(out: ActorRef, backend: ActorRef, userId: String, model: String) = Props(new UserFrontend(out, backend, userId, model))
}

class UserFrontend(out: ActorRef, backend: ActorRef, userId: String, model: String) extends Actor with ActorLogging {
  private val instance = ModelUser(self, userId, model)
  private val registerTask = context.system.scheduler.schedule(1.seconds, 30.seconds, self, RegisterUserFrontend)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case RegisterUserFrontend =>
      backend ! MessageEnvelope(userId, Connected(instance))
    case request: UserRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: UserResponse =>
      out ! response
  }
}
