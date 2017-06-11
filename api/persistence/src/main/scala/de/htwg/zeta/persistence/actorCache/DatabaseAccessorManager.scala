package de.htwg.zeta.persistence.actorCache

import java.util.concurrent.TimeUnit

import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import models.entity.Entity

/**
 */
class DatabaseAccessorManager[E <: Entity](private val system: ActorSystem) {
  private val actor: ActorRef = system.actorOf(Props[DocumentAccessorManagerActor[Entity]])
  private val timeout = Timeout(1, TimeUnit.SECONDS)

  def getAllIDs(): Future[Seq[String]] = {
    ask(actor, DocumentAccessorManagerActor.GetAllIds)(timeout).mapTo[Seq[String]]
  }

  def getAccessor(id: String): DatabaseAccessor[E] = {
    new DatabaseAccessor(
      ask(actor, DocumentAccessorManagerActor.GetAccessor(id))(timeout).mapTo[ActorRef],
      system)
  }

}
