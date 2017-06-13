package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.transient.TransientRepository
import models.entity.User

/**
 * PersistenceMicroServiceTest.
 */
class RestrictedRepositorySpec extends RepositoryBehavior {

  val user = User(UUID.randomUUID, "FirstName", "LastName", "test@mail.com", activated = true)

  val repo = new TransientRepository
  repo.users.create(user)

  "persistenceMicroService" should behave like repositoryBehavior(AccessRestrictedRepository(user.id, repo))

}
