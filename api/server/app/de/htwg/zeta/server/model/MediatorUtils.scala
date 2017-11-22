package de.htwg.zeta.server.model

import akka.actor.ActorRef

object MediatorUtils {

  case class MediatorMessage(msg: Any, broadcaster: ActorRef)
  case class SubscribeTo(dslType: String)

}

