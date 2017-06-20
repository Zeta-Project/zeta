package de.htwg.zeta.server.model.codeEditor

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSub
import de.htwg.zeta.common.models.entity.CodeDocument
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.EntityPersistence
import scalot.Server
import shared.CodeEditorMessage.DocAdded
import shared.CodeEditorMessage.DocDeleted
import shared.CodeEditorMessage.TextOperation


object CodeDocManagingActor {
  def props(): Props = Props(new CodeDocManagingActor())
}

/**
 * This Actor takes care of applying the changed to the documents.
 */
class CodeDocManagingActor extends Actor {

  private val persistence: EntityPersistence[CodeDocument] = Persistence.fullAccessRepository.codeDocument

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  def receive: Receive = {
    case TextOperation(op, docId) =>
      val target = sender()
      persistence.read(docId).flatMap { doc =>
        doc.serverDocument.receiveOperation(op) match {
          case Some(send) =>
            mediator ! Publish(
              doc.dslType,
              MediatorMessage(TextOperation(send, docId), self)
            )
            target ! MediatorMessage(TextOperation(send, docId), self)
          case _ => // Nothing to do!
        }
        persistence.update(docId, _ => doc)
      }

    case newDoc: DocAdded =>
      val target = sender()
      persistence.create(
        CodeDocument(
          id = newDoc.id,
          dslType = newDoc.dslType,
          metaModelId = newDoc.metaModelId,
          serverDocument = Server(
            str = "",
            title = newDoc.title,
            docType = newDoc.docType,
            id = newDoc.id.toString
          )
        )
      ).map { _ =>
        mediator ! Publish(newDoc.dslType, MediatorMessage(newDoc, target))
        sender() ! MediatorMessage(newDoc, target)
      }

    case msg: DocDeleted =>
      val target = sender()
      persistence.delete(msg.id).map { _ =>
        mediator ! Publish(msg.dslType, MediatorMessage(msg, target))
        sender() ! MediatorMessage(msg, target)
      }
  }

}
