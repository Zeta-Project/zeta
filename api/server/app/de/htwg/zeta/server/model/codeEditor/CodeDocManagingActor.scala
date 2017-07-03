package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import scala.collection.mutable
import akka.actor.ActorLogging
import akka.actor.Props
import de.htwg.zeta.server.actor.AbstractMediatorActor
import de.htwg.zeta.shared.CodeEditorMessage.DocAdded
import de.htwg.zeta.shared.CodeEditorMessage.DocDeleted
import de.htwg.zeta.shared.CodeEditorMessage.TextOperation
import scalot.Server
import scalot.Operation

object CodeDocManagingActor {

  case class SubscribeTo(dslType: String)

  def props(): Props = Props(new CodeDocManagingActor())
}

/**
 * This Actor takes care of applying the changed to the documents.
 */
class CodeDocManagingActor extends AbstractMediatorActor with ActorLogging {

  private val codeDocuments: mutable.Set[CodeDocument] = mutable.Set.empty

  override def childReceive: Receive = {
    case TextOperation(op, docId) => handleTextOperation(op, docId)
    case newDoc: DocAdded => handleDocAdded(newDoc)
    case msg: DocDeleted => handleDocDeleted(msg)
  }


  private def handleTextOperation(op: Operation, docId: UUID): Unit = {
    codeDocuments.find(_.id == docId).fold() { doc =>
      doc.serverDocument.receiveOperation(op) match {
        case Some(send) =>
          publish(
            doc.dslType,
            MediatorMessage(TextOperation(send, docId), self)
          )
          sender() ! MediatorMessage(TextOperation(send, docId), self)
        case _ => // Nothing to do!
      }
    }
  }

  private def handleDocAdded(newDoc: DocAdded): Unit = {
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
    publish(newDoc.dslType, MediatorMessage(newDoc, sender()))
    sender() ! MediatorMessage(newDoc, sender())
  }

  private def handleDocDeleted(msg: DocDeleted): Unit = {
    codeDocuments.find(_.id == msg.id).fold()(codeDocuments.remove)
    publish(msg.dslType, MediatorMessage(msg, sender()))
    sender() ! MediatorMessage(msg, sender())
  }

}
