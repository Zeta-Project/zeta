package de.htwg.zeta.persistence.accessor

import java.util.concurrent.TimeUnit

import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import de.htwg.zeta.persistence.actor.DocumentAccessorManagerActor
import models.document.Document

/**
 */
class DatabaseAccessorManager[T <: Document](private val system: ActorSystem) {
  private val actor: ActorRef = system.actorOf(Props[DocumentAccessorManagerActor[Document]])
  private val timeout = Timeout(1, TimeUnit.SECONDS)

  def getAllIDs(): Future[Seq[String]] = {
    ask(actor, DocumentAccessorManagerActor.GetAllIds)(timeout).mapTo[Seq[String]]
  }

  def getAccessor(id: String): DatabaseAccessor[T] = {
    new DatabaseAccessor(
      ask(actor, DocumentAccessorManagerActor.GetAccessor(id))(timeout).mapTo[ActorRef],
      system)
  }

}
