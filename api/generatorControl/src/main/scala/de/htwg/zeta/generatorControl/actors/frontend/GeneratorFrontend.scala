package de.htwg.zeta.generatorControl.actors.frontend

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ReceiveTimeout
import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.GeneratorClient
import de.htwg.zeta.common.models.frontend.GeneratorRequest
import de.htwg.zeta.common.models.frontend.GeneratorResponse
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManagerGenerator
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager

private case object RegisterGeneratorFrontend

/**
 * Actor to connect a generator to the backend
 */

object GeneratorFrontend extends FrontendManagerGenerator {

  val generatorFrontendService: String = "GeneratorFrontendService"

  case class CreateGeneratorFrontend(ident: UUID, out: ActorRef, userId: UUID, workId: UUID) extends FrontendManager.Create

  override def unapply(create: FrontendManager.Create): Option[(UUID, (ActorRef) => Props)] = create match {
    case CreateGeneratorFrontend(ident, out, userId, workId) => Some((ident, devMed => props(out, devMed, userId, workId)))
    case _ => None
  }

  def props(out: ActorRef, devMediator: ActorRef, userId: UUID, workId: UUID) = Props(new GeneratorFrontend(out, devMediator, userId, workId))
}

class GeneratorFrontend(out: ActorRef, devMediator: ActorRef, userId: UUID, workId: UUID) extends Actor with ActorLogging {
  context.setReceiveTimeout(Duration(10, TimeUnit.MINUTES))
  private val instance = GeneratorClient(out, workId)
  private val registerTask = context.system.scheduler.schedule(Duration(1, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), self, RegisterGeneratorFrontend)

  override def postStop() = {
    devMediator ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case FrontendManager.KeepAlive => // this resets receive timeout
    case ReceiveTimeout | FrontendManager.Terminate => context.stop(self)
    case RegisterGeneratorFrontend =>
      devMediator ! MessageEnvelope(userId, Connected(instance))
    case request: GeneratorRequest =>
      devMediator ! MessageEnvelope(userId, request)
    case response: GeneratorResponse =>
      out ! response
  }
}
