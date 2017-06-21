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
import akka.actor.Cancellable
import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.DeveloperRequest
import de.htwg.zeta.common.models.frontend.DeveloperResponse
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.common.models.frontend.ToolDeveloper
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManagerGenerator
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager

private case object RegisterDeveloperFrontend


/**
 * Actor to connect a tool developer to the backend
 */

object DeveloperFrontend extends FrontendManagerGenerator {

  val developerFrontendService: String = "DeveloperFrontendService"

  case class CreateDeveloperFrontend(ident: UUID, out: ActorRef, userId: UUID) extends FrontendManager.Create

  def props(out: ActorRef, devMediator: ActorRef, userId: UUID) = Props(new DeveloperFrontend(out, devMediator, userId))

  override def unapply(create: FrontendManager.Create): Option[(UUID, (ActorRef) => Props)] = create match {
    case CreateDeveloperFrontend(ident, out, userId) => Some((ident, devMed => props(out, devMed, userId)))
    case _ => None
  }
}

class DeveloperFrontend(out: ActorRef, devMediator: ActorRef, userId: UUID) extends Actor with ActorLogging {
  context.setReceiveTimeout(Duration(10, TimeUnit.MINUTES))
  log.error("developerFrontendCreated")
  private val instance = ToolDeveloper(out, userId)
  private val registerTask: Cancellable =
    context.system.scheduler.schedule(Duration(1, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), self, RegisterDeveloperFrontend)

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
    case RegisterDeveloperFrontend =>
      devMediator ! MessageEnvelope(userId, Connected(instance))
    case request: DeveloperRequest =>
      devMediator ! MessageEnvelope(userId, request)
    case response: DeveloperResponse =>
      out ! response
  }
}
