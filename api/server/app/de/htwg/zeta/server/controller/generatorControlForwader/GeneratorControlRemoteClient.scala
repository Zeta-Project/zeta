package de.htwg.zeta.server.controller.generatorControlForwader

import javax.inject.Inject
import javax.inject.Singleton

import akka.actor.ActorSystem
import de.htwg.zeta.generatorControl.actors.frontend.DeveloperFrontend
import de.htwg.zeta.generatorControl.actors.frontend.GeneratorFrontend
import de.htwg.zeta.generatorControl.actors.frontend.UserFrontend

/**
 */
@Singleton
class GeneratorControlRemoteClient @Inject()(system: ActorSystem, settings: RemoteClientSettings) {
  val developerFrontendService: RemoteClient = RemoteClient(system, DeveloperFrontend.developerFrontendService, settings)
  val generatorFrontendService: RemoteClient = RemoteClient(system, GeneratorFrontend.generatorFrontendService, settings)
  val userFrontendService: RemoteClient = RemoteClient(system, UserFrontend.userFrontendService, settings)

}

