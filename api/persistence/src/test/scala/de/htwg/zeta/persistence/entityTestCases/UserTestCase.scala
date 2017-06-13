package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.User


object UserTestCase extends EntityTestCase[User] {

  override val entity1 = User(
    id = UUID.randomUUID,
    firstName = "user1First",
    lastName = "user1Last",
    email = "user@one.com",
    activated = false
  )

  override val entity2 = User(
    id = UUID.randomUUID,
    firstName = "user2First",
    lastName = "user2Last",
    email = "user@two.com",
    activated = false
  )

  override val entity2Updated: User = entity2.copy(activated = true)

  override val entity3 = User(
    id = UUID.randomUUID,
    firstName = "user3First",
    lastName = "user3Last",
    email = "user@three.com",
    activated = false
  )

}
