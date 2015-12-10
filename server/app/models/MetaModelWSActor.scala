package models

import akka.actor.{Actor, ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import play.api.Logger
import play.api.libs.json.JsValue

class MetaModelWSActor(out: ActorRef, metaModelUuid: String) extends Actor {

  val log = Logger(this getClass() getName())
  val mediator = DistributedPubSubExtension(context.system).mediator

  mediator ! Subscribe(metaModelUuid, self)

  override def receive = {
    case msg: JsValue => mediator ! Publish(metaModelUuid, MediatorMessage(msg, self))
    case msg: MediatorMessage => if (msg.broadcaster != self) out ! msg.msg
    case ack: SubscribeAck => log.info(s"Subscribed to ${ack.subscribe.topic}")
    case _ => log.error("Got unknown message")
  }
}

object MetaModelWSActor {
  def props(out: ActorRef, metaModelUuid: String) = Props(new MetaModelWSActor(out, metaModelUuid))
}