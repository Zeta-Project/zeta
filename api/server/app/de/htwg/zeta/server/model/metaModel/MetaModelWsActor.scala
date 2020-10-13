package de.htwg.zeta.server.model.metaModel

import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import de.htwg.zeta.server.model.MediatorUtils.MediatorMessage
import de.htwg.zeta.server.model.MediatorUtils.SubscribeTo
import de.htwg.zeta.server.model.metaModel.MetaModelWsMediatorActor.Publish
import play.api.Logger
import play.api.libs.json.JsValue

object MetaModelWsActor {
  def props(out: ActorRef, metaModelUuid: UUID, mediator: MetaModelWsMediatorContainer): Props = Props(new MetaModelWsActor(out, metaModelUuid, mediator))
}

class MetaModelWsActor(out: ActorRef, metaModelId: UUID, mediatorContainer: MetaModelWsMediatorContainer) extends Actor {

  val log: Logger = Logger(getClass.getName)
  val mediator: ActorRef = mediatorContainer.mediator

  mediator ! SubscribeTo(metaModelId.toString)

  /**
   * Send an incoming WebSocket message to all other subscribed WebSocket actors.
   * Every actor, except of the broadcaster itself, forwards the received message to its client.
   */
  override def receive: Receive = {
    case msg: JsValue => mediator ! Publish(metaModelId.toString, MediatorMessage(msg, self))
    case msg: MediatorMessage => if (msg.broadcaster != self) out ! msg.msg

    case ack: SubscribeAck => log.info(s"Subscribed to ${ack.subscribe.topic}")
    case _ => log.error("Got unknown message")
  }

}
