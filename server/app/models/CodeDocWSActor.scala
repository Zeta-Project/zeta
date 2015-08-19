package models

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe}
import akka.event.Logging
import scalot.Server
import shared.CodeEditorMessage
import shared.CodeEditorMessage.{DocDeleted, DocAdded, TextOperation}
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
                documents(docId).diagramId,
                MediatorMessage(TextOperation(send, docId), self)
              )
            case _ => // Nothing to do!
          }
          CodeDocumentDB.saveDocument(documents(docId))

        case newDoc: DocAdded =>
          println("Manager: Got DocAdded")
          documents = documents + (newDoc.id -> new DBCodeDocument(
            docId = newDoc.id,
            diagramId = newDoc.diagramId,
            doc = new Server(
              str = "",
              title = newDoc.title,
              docType = newDoc.docType,
              id = newDoc.id)
          ))
          println(s"Manager: Added, publishing to ${newDoc.diagramId}")
          CodeDocumentDB.saveDocument(documents(newDoc.id))
          mediator ! Publish(newDoc.diagramId, MediatorMessage(newDoc, sender()))

        case msg: DocDeleted =>
          documents = documents - msg.id
          CodeDocumentDB.deleteDocWithId(msg.id)
          mediator ! Publish(msg.diagramId, MediatorMessage(msg, sender()))
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
class CodeDocWSActor(out: ActorRef, docManager: ActorRef, diagramId: String) extends Actor with ActorLogging {

  val mediator = DistributedPubSubExtension(context.system).mediator
  mediator ! Subscribe(diagramId, self)

  /** Tell the client about the existing documents */
  for (doc <- CodeDocumentDB.getDocsWithDiagramId(diagramId)) {
    println("Sending docadded message!"+  CodeDocumentDB.getDocsWithDiagramId(diagramId).length)

    out ! write[CodeEditorMessage](
      DocAdded(str = doc.doc.str,
        revision = doc.doc.operations.length,
        docType = doc.doc.docType,
        title = doc.doc.title,
        id = doc.docId,
        diagramId = doc.diagramId))
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
  def props(out: ActorRef, docManager: ActorRef, diagramId: String) = Props(new CodeDocWSActor(out, docManager, diagramId))
}

