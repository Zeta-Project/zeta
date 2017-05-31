package actors.frontend

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import models.frontend.Connected
import models.frontend.Disconnected
import models.frontend.GeneratorClient
import models.frontend.GeneratorRequest
import models.frontend.GeneratorResponse
import models.frontend.MessageEnvelope
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

private case object RegisterGeneratorFrontend

/**
 * Actor to connect a generator to the backend
 */

object GeneratorFrontend {
  def props(out: ActorRef, backend: ActorRef, userId: UUID, workId: String) = Props(new GeneratorFrontend(out, backend, userId, workId))
}

class GeneratorFrontend(out: ActorRef, backend: ActorRef, userId: UUID, workId: String) extends Actor with ActorLogging {
  private val instance = GeneratorClient(out, workId)
  private val registerTask = context.system.scheduler.schedule(Duration(1, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), self, RegisterGeneratorFrontend)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
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
