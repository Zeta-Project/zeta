package de.htwg.zeta.server.controller.generatorControlForwader

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props


class DirectForwarder(to: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: Any => to.tell(msg, context.parent)
  }
}

object DirectForwarder {
  def props(to: ActorRef): Props = Props(new DirectForwarder(to))
}
