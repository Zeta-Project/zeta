package de.htwg.zeta.server.model.metaModel

import javax.inject.Inject
import javax.inject.Singleton

import akka.actor.ActorRef
import akka.actor.ActorSystem

@Singleton
class MetaModelWsMediatorContainer @Inject()(private val system: ActorSystem) {
  val mediator: ActorRef = system.actorOf(MetaModelWsMediatorActor.props(), "metaModelWsMediator")
}