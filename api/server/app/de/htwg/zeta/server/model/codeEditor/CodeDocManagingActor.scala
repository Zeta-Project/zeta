package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Terminated
import scalot.Server
import scalot.Operation
import shared.CodeEditorMessage.DocAdded
import shared.CodeEditorMessage.DocDeleted
import shared.CodeEditorMessage.TextOperation


object CodeDocManagingActor {

  case class SubscribeTo(dslType: String)

  def props(): Props = Props(new CodeDocManagingActor())
}

/**
 * This Actor takes care of applying the changed to the documents.
 */
class CodeDocManagingActor extends Actor with ActorLogging {

  private val codeDocuments: mutable.Set[CodeDocument] = mutable.Set.empty
  private val subscriberCache: mutable.HashMap[String, mutable.HashSet[ActorRef]] = mutable.HashMap.empty


  def receive: Receive = {
    case TextOperation(op, docId) => handleTextOperation(op, docId)
    case newDoc: DocAdded => handleDocAdded(newDoc)
    case msg: DocDeleted => handleDocDeleted(msg)
    case CodeDocManagingActor.SubscribeTo(dslType) => handleSubscribeTo(dslType)
    case Terminated(deadRef) => handleSubscriberDeath(deadRef)
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


  private def publish(dslType: String, msg: Any): Unit = {
    subscriberCache.get(dslType).foreach(_.foreach(_.tell(msg, self)))
  }

  private def handleSubscribeTo(dslType: String): Unit = {
    val set = subscriberCache.getOrElseUpdate(dslType, mutable.HashSet.empty)
    val sub = sender()
    context.watch(sub)
    set.add(sender())
  }

  private def handleSubscriberDeath(deadRef: ActorRef): Unit = {
    var toDelete: List[String] = Nil
    subscriberCache.foreach(pair => {
      val key = pair._1
      val set = pair._2

      if (set.remove(deadRef) && set.isEmpty) {
        // safe to var. Do not alter map while iterating over it.
        toDelete = key :: toDelete
      }
    })

    toDelete.foreach(subscriberCache.remove)
  }

}
