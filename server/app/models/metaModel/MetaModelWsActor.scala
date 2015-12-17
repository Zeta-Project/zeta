package models.metaModel

import akka.actor.{Actor, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import models.codeEditor.MediatorMessage
import play.api.Logger
import play.api.libs.json.JsValue

class MetaModelWsActor(out: ActorRef, metaModelUuid: String) extends Actor {

  val log = Logger(getClass getName)
  val mediator = DistributedPubSubExtension(context.system).mediator

  mediator ! Subscribe(metaModelUuid, self)

  /**
    * Send an incoming WebSocket message to all other subscribed WebSocket actors.
    * Every actor, except of the broadcaster itself, forwards the received message to its client.
    */
  override def receive = {
    case msg: JsValue => mediator ! Publish(metaModelUuid, MediatorMessage(msg, self))
    case msg: MediatorMessage => if (msg.broadcaster != self) out ! msg.msg

    case ack: SubscribeAck => log.info(s"Subscribed to ${ack.subscribe.topic}")
    case _ => log.error("Got unknown message")
  }
}

object MetaModelWsActor {
  def props(out: ActorRef, metaModelUuid: String) = Props(new MetaModelWsActor(out, metaModelUuid))
}