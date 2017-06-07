package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.event.Logging
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

  var documents: Map[UUID, DbCodeDocument] = CodeDocumentDb.getAllDocuments.map(x => (x.docId, x)).toMap

  val mediator = DistributedPubSub(context.system).mediator
  val log = Logging(context.system, this)

  def receive = {
    case x: CodeEditorMessage =>
      x match {
        case TextOperation(op, docId) =>
          documents(docId).doc.receiveOperation(op) match {
            case Some(send) => {
              mediator ! Publish(
                documents(docId).dslType,
                MediatorMessage(TextOperation(send, docId), self)
              )
              sender() ! MediatorMessage(TextOperation(send, docId), self)
            }
            case _ => // Nothing to do!
          }
          CodeDocumentDb.saveDocument(documents(docId))

        case newDoc: DocAdded =>
          documents = documents + (newDoc.id -> DbCodeDocument(
            docId = newDoc.id,
            dslType = newDoc.dslType,
            metaModelId = newDoc.metaModelId,
            doc =  Server(
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

        case _ => ;
      }
  }
}

object CodeDocManagingActor {
  def props() = Props(new CodeDocManagingActor())
}

/**
 * This Actor is responsible of the communictaion with the users browser
 */
class CodeDocWsActor(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(dslType, self)

  /** Tell the client about the existing document */
  CodeDocumentDb.getDocWithUuidAndDslType(metaModelId, dslType) match {
    case doc: Some[DbCodeDocument] => out ! default.write[CodeEditorMessage](
      DocLoaded(
        str = doc.get.doc.str,
        revision = doc.get.doc.operations.length,
        docType = doc.get.doc.docType,
        title = doc.get.doc.title,
        id = doc.get.docId,
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

  private def processCommand(pickled: String) = {
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

  private def processMediatorMessage(medMsg: MediatorMessage) = {
    if (medMsg.broadcaster != self) {
      medMsg.msg match {
        case x: CodeEditorMessage => out ! default.write[CodeEditorMessage](x)
        case _ => log.error("Unknown message type from Meidator")
      }
    }
  }
}

object CodeDocWsActor {
  def props(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String) = Props(new CodeDocWsActor(out, docManager, metaModelId, dslType))
}
