package models

import akka.actor.{Props, ActorRef, Actor}
import play.api.Logger


class MetaModelWSActor(out: ActorRef, metaModelUuid: String) extends Actor {

  val log = Logger(this getClass() getName())

  override def receive = {
    case _ => log error "Not yet implemented"
  }
}

object MetaModelWSActor {
  def props(out: ActorRef, metaModelUuid: String) = Props(new MetaModelWSActor(out, metaModelUuid))
}