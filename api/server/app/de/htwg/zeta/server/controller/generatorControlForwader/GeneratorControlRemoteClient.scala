package de.htwg.zeta.server.controller.generatorControlForwader

import javax.inject.Inject
import javax.inject.Singleton

import akka.actor.ActorSystem
import de.htwg.zeta.common.cluster.RemoteClient
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperFrontend
import de.htwg.zeta.generatorControl.actors.frontend.GeneratorFrontend
import de.htwg.zeta.generatorControl.actors.frontend.UserFrontend

@Singleton
class GeneratorControlRemoteClient @Inject()(system: ActorSystem, settings: RemoteClient) {
  val developerFrontendService: RemoteService = RemoteService(system, DeveloperFrontend.serviceName, settings)
  val generatorFrontendService: RemoteService = RemoteService(system, GeneratorFrontend.serviceName, settings)
  val userFrontendService: RemoteService = RemoteService(system, UserFrontend.serviceName, settings)

}

