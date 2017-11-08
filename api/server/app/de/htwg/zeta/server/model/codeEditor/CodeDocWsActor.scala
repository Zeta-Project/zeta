package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.shared.CodeEditorMessage
import de.htwg.zeta.shared.CodeEditorMessage.DocAdded
import de.htwg.zeta.shared.CodeEditorMessage.DocDeleted
import de.htwg.zeta.shared.CodeEditorMessage.DocLoaded
import de.htwg.zeta.shared.CodeEditorMessage.DocNotFound
import de.htwg.zeta.shared.CodeEditorMessage.TextOperation
import upickle.default


object CodeDocWsActor {
  def props(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String, metaModelEntityRepo: EntityRepository[MetaModelEntity]): Props = {
    Props(new CodeDocWsActor(out, docManager, metaModelId, dslType, metaModelEntityRepo))
  }
}

/**
 * This Actor is responsible of the communication with the users browser
 */
class CodeDocWsActor(
    out: ActorRef,
    docManager: ActorRef,
    metaModelId: UUID,
    dslType: String,
    metaModelEntityRepo: EntityRepository[MetaModelEntity]
) extends Actor with ActorLogging {

  docManager ! CodeDocManagingActor.SubscribeTo(dslType)

  /** Tell the client about the existing document */
  metaModelEntityRepo.read(metaModelId).map { metaModelEntity: MetaModelEntity =>

    out ! default.write[CodeEditorMessage](
      DocLoaded(
        str = dslType match {
          case "style" => metaModelEntity.dsl.style.fold("")(_.code)
          case "shape" => metaModelEntity.dsl.shape.fold("")(_.code)
          case "diagram" => metaModelEntity.dsl.diagram.fold("")(_.code)
        },
        revision = 0,
        docType = dslType,
        title = metaModelId.toString,
        id = UUID.randomUUID(),
        dslType = dslType,
        metaModelId = metaModelId
      )
    )
  }.recover { case _ =>
    out ! default.write[CodeEditorMessage](
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
        case _ => log.error("Unknown message type from Mediator")
      }
    }
  }
}
