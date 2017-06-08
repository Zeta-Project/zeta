package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.transient.TransientRepository
import models.User

/**
 * PersistenceMicroServiceTest.
 */
class RestrictedPersistenceSpec extends RepositoryBehavior {

  val user = User(UUID.randomUUID, "FirstName", "LastName", "test@mail.com", activated = true)

  val repo = new TransientRepository
  repo.users.create(user)

  "persistenceMicroService" should behave like serviceBehavior(AccessRestrictedRepository(user.id, repo))

}
