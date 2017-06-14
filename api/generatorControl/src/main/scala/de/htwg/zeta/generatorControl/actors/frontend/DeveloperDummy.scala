package de.htwg.zeta.generatorControl.actors.frontend

import java.util.concurrent.TimeUnit

import de.htwg.zeta.generatorControl.actors.developer.Mediator
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props

import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.DeveloperRequest
import de.htwg.zeta.common.models.frontend.DeveloperResponse
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.MessageEnvelope
import de.htwg.zeta.common.models.frontend.ToolDeveloper

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import akka.cluster.sharding.ClusterSharding

private case object RegisterDeveloperDummy

/**
 * Actor to connect a tool developer to the backend
 */

object DeveloperDummy {
  def props() = Props(new DeveloperDummy())
}

class DeveloperDummy() extends Actor with ActorLogging {
  private val userId = "modigen"

  private val backend: ActorRef = ClusterSharding(context.system).shardRegion(Mediator.shardRegionName)

  private val instance = ToolDeveloper(self, userId)
  private val registerTask = context.system.scheduler.schedule(Duration(1, TimeUnit.SECONDS), Duration(10, TimeUnit.SECONDS), self, RegisterDeveloperDummy)

  override def postStop() = {
    backend ! MessageEnvelope(userId, Disconnected(instance))
    registerTask.cancel()
  }
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case RegisterDeveloperDummy =>
      backend ! MessageEnvelope(userId, Connected(instance))
    case request: DeveloperRequest =>
      backend ! MessageEnvelope(userId, request)
    case response: DeveloperResponse =>
      log.info("Message from backend [{}] ", response.toString)
  }
}
