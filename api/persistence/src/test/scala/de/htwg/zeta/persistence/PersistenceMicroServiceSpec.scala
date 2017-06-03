package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import de.htwg.zeta.persistence.microService.PersistenceMicroService
import de.htwg.zeta.persistence.microService.PersistenceServer
import de.htwg.zeta.persistence.transientCache.TransientPersistenceService

/**
 * PersistenceMicroServiceTest.
 */
class PersistenceMicroServiceSpec extends PersistenceServiceBehavior {

  private val address = "localhost"
  private val port = 39239

  Await.result(PersistenceServer.start(address, port, new TransientPersistenceService), Duration(1, TimeUnit.MINUTES))

  "persistenceMicroService" should behave like serviceBehavior(new PersistenceMicroService(address, port))

}
