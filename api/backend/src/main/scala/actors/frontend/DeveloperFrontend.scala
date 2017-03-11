package actors.frontend

import akka.actor._
import models.frontend._
import scala.concurrent.duration._

/**
 * Actor to connect a tool developer to the backend
 */

object DeveloperFrontend {
  def props(out: ActorRef, backend: ActorRef, userId: String) = Props(new DeveloperFrontend(out, backend, userId))

  private case object Register
}

class DeveloperFrontend(out: ActorRef, backend: ActorRef, userId: String) extends Actor with ActorLogging {
  import DeveloperFrontend._
  import context.dispatcher

  val instance = ToolDeveloper(out, userId)
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
    case request: DeveloperRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: DeveloperResponse =>
      out ! response
  }
}
