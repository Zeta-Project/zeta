package de.htwg.zeta.server.model.codeEditor

import akka.actor.ActorRef

case class MediatorMessage(msg: Any, broadcaster: ActorRef)
