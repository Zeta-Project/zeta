package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.transient.TransientRepository

/**
 * PersistenceMicroServiceTest.
 */
class TransientRepositorySpec extends RepositoryBehavior {

  "TransientRepository" should behave like repositoryBehavior(new TransientRepository, restricted = false)

}
