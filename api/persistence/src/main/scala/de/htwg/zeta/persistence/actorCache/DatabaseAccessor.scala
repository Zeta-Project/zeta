package de.htwg.zeta.persistence.actorCache

import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import de.htwg.zeta.common.models.entity.Entity


/**
 */
class DatabaseAccessor[E <: Entity](private val actor: Future[ActorRef], private val system: ActorSystem) {
  private val timeout = Timeout(1, TimeUnit.SECONDS)
  private val dispatcher: ExecutionContextExecutor = system.dispatcher


  def ReadDocument: Future[E] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.ReadDocument)(timeout).map {
      case DocumentAccessorActor.ReadingDocumentSucceed(doc: E) => doc
      case DocumentAccessorActor.ReadingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def CreateDocument(doc: E): Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.CreateDocument)(timeout).map {
      case DocumentAccessorActor.CreatingDocumentSucceed =>
      case DocumentAccessorActor.CreatingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def UpdateDocument(doc: E): Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.UpdateDocument)(timeout).map {
      case DocumentAccessorActor.UpdatingDocumentSucceed =>
      case DocumentAccessorActor.UpdatingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

  def DeleteDocument: Future[Any] = {
    actor.flatMap(a => ask(a, DocumentAccessorActor.DeleteDocument)(timeout).map {
      case DocumentAccessorActor.DeletingDocumentSucceed =>
      case DocumentAccessorActor.DeletingDocumentFailed(msg: String) => throw new IllegalArgumentException(msg) // TODO Change
    }(dispatcher))(dispatcher)
  }

}
