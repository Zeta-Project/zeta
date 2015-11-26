package models

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe}
import akka.event.Logging
import scalot.Server
import shared.CodeEditorMessage
import shared.CodeEditorMessage._
import upickle.default._
import play.api.libs.concurrent.Akka
import play.api.Play.current


case class MediatorMessage(msg: Any, broadcaster: ActorRef)


/**
  * This Actor takes care of applying the changed to the documents.
  */
class CodeDocManagingActor extends Actor {

  var documents: Map[String, DBCodeDocument] = CodeDocumentDB.getAllDocuments.map(x => (x.docId, x)).toMap

  val mediator = DistributedPubSubExtension(context.system).mediator
  val log = Logging(context.system, this)

  def receive = {
    case x: CodeEditorMessage =>
      x match {
        case TextOperation(op, docId) =>
          documents(docId).doc.receiveOperation(op) match {
            case Some(send) =>
              mediator ! Publish(
                documents(docId).dslType,
                MediatorMessage(TextOperation(send, docId), self)
              )
            case _ => // Nothing to do!
          }
          CodeDocumentDB.saveDocument(documents(docId))

        case newDoc: DocAdded =>
          println("Manager: Got DocAdded")
          documents = documents + (newDoc.id -> new DBCodeDocument(
            docId = newDoc.id,
            dslType = newDoc.dslType,
            metaModelUuid = newDoc.metaModelUuid,
            doc = new Server(
              str = "",
              title = newDoc.title,
              docType = newDoc.docType,
              id = newDoc.id)
          ))
          println(s"Manager: Added, publishing to ${newDoc.dslType}")
          CodeDocumentDB.saveDocument(documents(newDoc.id))
          mediator ! Publish(newDoc.dslType, MediatorMessage(newDoc, sender()))

        case msg: DocDeleted =>
          documents = documents - msg.id
          CodeDocumentDB.deleteDocWithId(msg.id)
          mediator ! Publish(msg.dslType, MediatorMessage(msg, sender()))
      }
  }
}

object CodeDocManagingActor {
  private val codeDocManager: ActorRef = Akka.system.actorOf(Props[CodeDocManagingActor], name = "codeDocManager")

  def getCodeDocManager = codeDocManager
}


/**
  * This Actor is responsible of the communictaion with the users browser
  */
class CodeDocWSActor(out: ActorRef, docManager: ActorRef, metaModelUuid: String, dslType: String) extends Actor with ActorLogging {

  val mediator = DistributedPubSubExtension(context.system).mediator
  mediator ! Subscribe(dslType, self)

  /** Tell the client about the existing document */
  for (doc <- CodeDocumentDB.getDocsWithDslType(dslType)) {
    println("Sending docadded message!" + CodeDocumentDB.getDocsWithDslType(dslType).length)
    out ! write[CodeEditorMessage](
      DocAdded(str = doc.doc.str,
        revision = doc.doc.operations.length,
        docType = doc.doc.docType,
        title = doc.doc.title,
        id = doc.docId,
        dslType = doc.dslType,
        metaModelUuid = doc.metaModelUuid))
  }

  CodeDocumentDB.getDocWithUuidAndDslType(metaModelUuid, dslType) match {
    case doc: Some[DBCodeDocument] => out ! write[CodeEditorMessage](
      DocLoaded(
        str = doc.get.doc.str,
        revision = doc.get.doc.operations.length,
        docType = doc.get.doc.docType,
        title = doc.get.doc.title,
        id = doc.get.docId,
        dslType = doc.get.dslType,
        metaModelUuid = doc.get.metaModelUuid
      )
    )
    case None => out ! write[CodeEditorMessage](
      DocNotFound(
        dslType = dslType,
        metaModelUuid = metaModelUuid
      )
    )
  }


  def receive = {
    case pickled: String => try {
      read[CodeEditorMessage](pickled) match {

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

    case medMsg: MediatorMessage =>
      if (medMsg.broadcaster != self) {
        medMsg.msg match {
          case x: CodeEditorMessage => out ! write[CodeEditorMessage](x)
          case _ => log.error("Unknown message type from Meidator")
        }
      }

    case _ => log.debug(s" ${self.toString()} - Message is not a String!")
  }

  override def preStart() = {
    println("Started Actor!")
  }
}

object CodeDocWSActor {
  def props(out: ActorRef, docManager: ActorRef, metaModelUuid: String, dslType: String) = Props(new CodeDocWSActor(out, docManager, metaModelUuid, dslType))
}

