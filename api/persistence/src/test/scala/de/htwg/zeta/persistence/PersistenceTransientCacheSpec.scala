package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transientCache.PersistenceTransientCacheService

/**
 * PersistenceMicroServiceTest.
 */
class PersistenceTransientCacheSpec extends PersistenceServiceBehavior {

  "persistenceMicroService" should behave like serviceBehavior(new PersistenceTransientCacheService)

}
