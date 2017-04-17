package actors.frontend

import akka.actor._
import models.frontend._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

private case object RegisterGeneratorFrontend

/**
 * Actor to connect a generator to the backend
 */

object GeneratorFrontend {
  def props(out: ActorRef, backend: ActorRef, userId: String, workId: String) = Props(new GeneratorFrontend(out, backend, userId, workId))
}

class GeneratorFrontend(out: ActorRef, backend: ActorRef, userId: String, workId: String) extends Actor with ActorLogging {
  private val instance = GeneratorClient(out, workId)
  private val registerTask = context.system.scheduler.schedule(1.seconds, 30.seconds, self, RegisterGeneratorFrontend)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case RegisterGeneratorFrontend =>
      backend ! MessageEnvelope(userId, Connected(instance))
    case request: GeneratorRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: GeneratorResponse =>
      out ! response
  }
}
