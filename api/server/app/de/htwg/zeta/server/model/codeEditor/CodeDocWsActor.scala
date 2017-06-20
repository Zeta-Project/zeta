package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import de.htwg.zeta.common.models.entity.CodeDocument
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.EntityPersistence
import scalot.Server
import shared.CodeEditorMessage
import shared.CodeEditorMessage.DocAdded
import shared.CodeEditorMessage.DocDeleted
import shared.CodeEditorMessage.DocLoaded
import shared.CodeEditorMessage.DocNotFound
import shared.CodeEditorMessage.TextOperation
import upickle.default

case class MediatorMessage(msg: Any, broadcaster: ActorRef)

/**
 * This Actor takes care of applying the changed to the documents.
 */
class CodeDocManagingActor extends Actor {

  private val persistence: EntityPersistence[CodeDocument] = Persistence.fullAccessRepository.codeDocument

  var documents: Map[UUID, CodeDocument] = CodeDocumentDb.getAllDocuments.map(x => (x.id, x)).toMap

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  def receive: Receive = {
    case TextOperation(op, docId) =>
      documents(docId).serverDocument.receiveOperation(op) match {
        case Some(send) =>
          mediator ! Publish(
            documents(docId).dslType,
            MediatorMessage(TextOperation(send, docId), self)
          )
          sender() ! MediatorMessage(TextOperation(send, docId), self)
        case _ => // Nothing to do!
      }

      CodeDocumentDb.saveDocument(documents(docId))

    case newDoc: DocAdded =>
      documents = documents + (newDoc.id -> CodeDocument(
        id = newDoc.id,
        dslType = newDoc.dslType,
        metaModelId = newDoc.metaModelId,
        serverDocument = Server(
          str = "",
          title = newDoc.title,
          docType = newDoc.docType,
          id = newDoc.id.toString
        )
      ))
      CodeDocumentDb.saveDocument(documents(newDoc.id))
      mediator ! Publish(newDoc.dslType, MediatorMessage(newDoc, sender()))
      sender() ! MediatorMessage(newDoc, sender())

    case msg: DocDeleted =>
      documents = documents - msg.id
      CodeDocumentDb.deleteDocWithId(msg.id)
      mediator ! Publish(msg.dslType, MediatorMessage(msg, sender()))
      sender() ! MediatorMessage(msg, sender())

  }

}

object CodeDocManagingActor {
  def props(): Props = Props(new CodeDocManagingActor())
}

/**
 * This Actor is responsible of the communication with the users browser
 */
class CodeDocWsActor(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String) extends Actor with ActorLogging {

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(dslType, self)

  /** Tell the client about the existing document */
  CodeDocumentDb.getDocWithIdAndDslType(metaModelId, dslType) match {
    case doc: Some[CodeDocument] => out ! default.write[CodeEditorMessage](
      DocLoaded(
        str = doc.get.serverDocument.str,
        revision = doc.get.serverDocument.operations.length,
        docType = doc.get.serverDocument.docType,
        title = doc.get.serverDocument.title,
        id = doc.get.id,
        dslType = doc.get.dslType,
        metaModelId = doc.get.metaModelId
      )
    )
    case None => out ! default.write[CodeEditorMessage](
      DocNotFound(
        dslType = dslType,
        metaModelId = metaModelId
      )
    )
  }

  override def receive: Actor.Receive = {
    case pickled: String => processCommand(pickled)
    case medMsg: MediatorMessage => processMediatorMessage(medMsg)
    case _ => log.debug(s" ${self.toString()} - Message is not a String!")
  }

  private def processCommand(pickled: String): Unit = {
    try {
      default.read[CodeEditorMessage](pickled) match {

        case msg: TextOperation =>
          docManager ! msg

        case msg: DocAdded =>
          log.debug("WS: Got DocAdded")
          docManager ! msg

        case msg: DocDeleted =>
          log.debug("WS: Got DocDeleted")
          docManager ! msg

        case _ => log.error("Discarding message, probably sent by myself")
      }
    }
  }

  private def processMediatorMessage(medMsg: MediatorMessage): Unit = {
    if (medMsg.broadcaster != self) {
      medMsg.msg match {
        case x: CodeEditorMessage => out ! default.write[CodeEditorMessage](x)
        case _ => log.error("Unknown message type from Meidator")
      }
    }
  }
}

object CodeDocWsActor {
  def props(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String): Props = {
    Props(new CodeDocWsActor(out, docManager, metaModelId, dslType))
  }
}
