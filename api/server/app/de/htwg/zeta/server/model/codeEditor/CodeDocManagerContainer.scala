package de.htwg.zeta.server.model.codeEditor

import javax.inject.Inject
import javax.inject.Singleton

import akka.actor.ActorRef
import akka.actor.ActorSystem

@Singleton
class CodeDocManagerContainer @Inject()(private val system: ActorSystem) {
  val manager: ActorRef = system.actorOf(CodeDocManagingActor.props(), "codeDocManager")
}
