package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.persistence.Persistence
import shared.CodeEditorMessage
import shared.CodeEditorMessage.DocAdded
import shared.CodeEditorMessage.DocDeleted
import shared.CodeEditorMessage.DocLoaded
import shared.CodeEditorMessage.DocNotFound
import shared.CodeEditorMessage.TextOperation
import upickle.default


object CodeDocWsActor {
  def props(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String): Props = {
    Props(new CodeDocWsActor(out, docManager, metaModelId, dslType))
  }
}

/**
 * This Actor is responsible of the communication with the users browser
 */
class CodeDocWsActor(out: ActorRef, docManager: ActorRef, metaModelId: UUID, dslType: String) extends Actor with ActorLogging {

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(dslType, self)

  /** Tell the client about the existing document */
  Persistence.fullAccessRepository.metaModelEntity.read(metaModelId).map { metaModelEntity: MetaModelEntity =>

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
