package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.User


object UserFixtures {

  val entity1 = User(
    id = UUID.randomUUID,
    firstName = "user1First",
    lastName = "user1Last",
    email = "user@one.com",
    activated = false
  )

  val entity2 = User(
    id = UUID.randomUUID,
    firstName = "user2First",
    lastName = "user2Last",
    email = "user@two.com",
    activated = false
  )

  val entity2Updated: User = entity2.copy(activated = true)

  val entity3 = User(
    id = UUID.randomUUID,
    firstName = "user3First",
    lastName = "user3Last",
    email = "user@three.com",
    activated = false
  )

}
