package de.htwg.zeta.generatorControl.actors.frontend

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.common.models.frontend.ModelUser
import de.htwg.zeta.common.models.frontend.UserRequest
import de.htwg.zeta.common.models.frontend.UserResponse

import scala.concurrent.duration.Duration
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
  private val registerTask = context.system.scheduler.schedule(Duration(1, TimeUnit.SECONDS), Duration(30, TimeUnit.SECONDS), self, RegisterUserFrontend)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
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
