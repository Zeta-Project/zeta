package de.htwg.zeta.server.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import de.htwg.zeta.server.silhouette.ZetaIdentity

object WebSocketActor {
  def props(user: ZetaIdentity, onlineUserManagerFactory: OnlineUserActorFactory)(out: ActorRef): Props =
    Props(new WebSocketActor(user, out, onlineUserManagerFactory.onlineActor))
}

class WebSocketActor(user: ZetaIdentity, out: ActorRef, manager: ActorRef) extends Actor {
  override def preStart(): Unit = {
    super.preStart()
    manager ! "Hello"
  }

  def receive: PartialFunction[Any, Unit] = {
    case msg: String =>
      out ! (s"Hi ${user.fullName}, I received your message: " + msg)
  }
}
