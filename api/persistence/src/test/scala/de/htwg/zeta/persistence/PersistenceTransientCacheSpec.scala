package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transientCache.TransientRepository

/**
 * PersistenceMicroServiceTest.
 */
class PersistenceTransientCacheSpec extends RepositoryBehavior {

  "persistenceMicroService" should behave like serviceBehavior(new TransientRepository)

}
