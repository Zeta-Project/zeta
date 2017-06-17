package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.actorCache.ActorCacheRepository
import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.transient.TransientRepository

/**
 * ActorCacheMicroServiceTest.
 */
class ActorCacheRepositorySpec extends RepositoryBehavior {

  "ActorCacheRepository" should behave like repositoryBehavior(new ActorCacheRepository(new TransientRepository), restricted = false)

}
