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
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.common.models.frontend.ModelUser
import de.htwg.zeta.common.models.frontend.UserRequest
import de.htwg.zeta.common.models.frontend.UserResponse
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManager
import de.htwg.zeta.generatorControl.actors.frontendManager.FrontendManagerGenerator

private case object RegisterUserFrontend

/**
 * Actor to connect a model user to the backend
 */

object UserFrontend extends FrontendManagerGenerator {

  val serviceName: String = "UserFrontendService"

  case class CreateUserFrontend(ident: UUID, out: ActorRef, userId: UUID, modelId: UUID) extends FrontendManager.Create

  override def unapply(create: FrontendManager.Create): Option[(UUID, (ActorRef) => Props)] = create match {
    case CreateUserFrontend(ident, out, userId, modelId) => Some((ident, devMed => props(out, devMed, userId, modelId)))
    case _ => None
  }

  def props(out: ActorRef, developerMediator: ActorRef, userId: UUID, modelId: UUID) = Props(new UserFrontend(out, developerMediator, userId, modelId))
}

class UserFrontend(out: ActorRef, backend: ActorRef, userId: UUID, model: UUID) extends Actor with ActorLogging {
  context.setReceiveTimeout(Duration(10, TimeUnit.MINUTES))
  private val instance = ModelUser(self, userId, model)
  private val registerTask = context.system.scheduler.scheduleAtFixedRate(Duration(1, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), self, RegisterUserFrontend)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }

  def receive = {
    case FrontendManager.KeepAlive => // this resets receive timeout
    case ReceiveTimeout | FrontendManager.Terminate => context.stop(self)
    case RegisterUserFrontend =>
      backend ! MessageEnvelope(userId, Connected(instance))
    case request: UserRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: UserResponse =>
      out ! response
  }
}
