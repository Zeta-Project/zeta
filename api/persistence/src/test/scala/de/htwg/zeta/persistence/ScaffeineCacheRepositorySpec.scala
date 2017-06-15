package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.scaffeineCache.ScaffeineCacheRepository
import de.htwg.zeta.persistence.transient.TransientRepository

/**
 * PersistenceMicroServiceTest.
 */
class ScaffeineCacheRepositorySpec extends RepositoryBehavior {

  "persistenceMicroService" should behave like repositoryBehavior(new ScaffeineCacheRepository(new TransientRepository), restricted = false)

}
