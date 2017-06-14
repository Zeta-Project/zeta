package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.GeneratorImage


object GeneratorImageFixtures {

  val entity1 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage1",
    dockerImage = "dockerImage1"
  )

  val entity2 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage2",
    dockerImage = "dockerImage2"
  )

  val entity2Updated: GeneratorImage = entity2.copy(name = "filterImage2Updated")

  val entity3 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage3",
    dockerImage = "dockerImage3"
  )

}
