package models

import akka.actor.{Props, ActorRef, Actor}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, SubscribeAck, Subscribe}
import play.api.Logger
import play.api.libs.json.JsValue


class MetaModelWSActor(out: ActorRef, metaModelUuid: String) extends Actor {

  val log = Logger(this getClass() getName())
  val mediator = DistributedPubSubExtension(context.system).mediator

  mediator ! Subscribe(metaModelUuid, self)

  override def receive = {

    case webSocketMsg: JsValue => (webSocketMsg \ "type").as[String] match {
      case "getGraph" =>
      case _ => mediator ! Publish(metaModelUuid, MediatorMessage(webSocketMsg, self))
    }

    case msg: MediatorMessage => out ! msg.msg
    case msg: SubscribeAck => log.info(s"Subscribed to ${msg.subscribe.topic}")
    case _ => log.error("Got unknown message")
  }
}

object MetaModelWSActor {
  def props(out: ActorRef, metaModelUuid: String) = Props(new MetaModelWSActor(out, metaModelUuid))
}