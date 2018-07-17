package de.htwg.zeta.server.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineUserActorFactory @Inject()(implicit actorSystem: ActorSystem) {
  val onlineActor: ActorRef = actorSystem.actorOf(OnlineUserActor.props())
}

class OnlineUserActor() extends Actor {
  override def receive: Receive = {
    case s: String => print(s)
  }
}
object OnlineUserActor {
  def props(): Props = Props(new OnlineUserActor())
}