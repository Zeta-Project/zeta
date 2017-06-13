package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transient.TransientRepository

/**
 * PersistenceMicroServiceTest.
 */
class TransientRepositorySpec extends RepositoryBehavior {

  "persistenceMicroService" should behave like serviceBehavior(new TransientRepository)

}
