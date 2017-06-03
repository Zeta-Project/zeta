package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transientCache.TransientPersistenceService

/**
 * PersistenceMicroServiceTest.
 */
class PersistenceTransientCacheSpec extends PersistenceServiceBehavior {

  "persistenceMicroService" should behave like serviceBehavior(new TransientPersistenceService)

}
