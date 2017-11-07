package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.behavior.RepositoryBehavior

/**
 * RestrictedRepositorySpec.
 */
class RestrictedRepositorySpec extends RepositoryBehavior {

  val user = User(UUID.randomUUID, "FirstName", "LastName", "test@mail.com", activated = true)

 /* TODO val repo = new TransientRepository
  repo.user.create(user)

  "AccessRestrictedRepository" should behave like repositoryBehavior(new AccessRestrictedRepository(user.id, repo), restricted = true)
*/
}
