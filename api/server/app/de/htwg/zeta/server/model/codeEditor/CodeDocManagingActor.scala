package de.htwg.zeta.server.model.codeEditor

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSub
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
class CodeDocManagingActor extends Actor with ActorLogging {

  private val codeDocuments: mutable.Set[CodeDocument] = mutable.Set.empty

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  def receive: Receive = {

    case TextOperation(op, docId) =>
      codeDocuments.find(_.id == docId).fold() { doc =>
        doc.serverDocument.receiveOperation(op) match {
          case Some(send) =>
            mediator ! Publish(
              doc.dslType,
              MediatorMessage(TextOperation(send, docId), self)
            )
            sender() ! MediatorMessage(TextOperation(send, docId), self)
          case _ => // Nothing to do!
        }
      }

    case newDoc: DocAdded =>
      codeDocuments +=
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
      mediator ! Publish(newDoc.dslType, MediatorMessage(newDoc, sender()))
      sender() ! MediatorMessage(newDoc, sender())

    case msg: DocDeleted =>
      codeDocuments.find(_.id == msg.id).fold()(codeDocuments.remove)
      mediator ! Publish(msg.dslType, MediatorMessage(msg, sender()))
      sender() ! MediatorMessage(msg, sender())

  }

}
