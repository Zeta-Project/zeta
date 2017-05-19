package de.htwg.zeta.server.model.metaModel

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck
import de.htwg.zeta.server.model.codeEditor.MediatorMessage
import play.api.Logger
import play.api.libs.json.JsValue

import scala.language.postfixOps

class MetaModelWsActor(out: ActorRef, metaModelUuid: String) extends Actor {

  val log = Logger(getClass getName)
  val mediator = DistributedPubSub(context.system).mediator

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
