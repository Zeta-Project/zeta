package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.Generator


object GeneratorFixtures {

  val entity1 = Generator(
    id = UUID.randomUUID,
    name = "filterImage1",
    imageId = UUID.randomUUID
  )

  val entity2 = Generator(
    id = UUID.randomUUID,
    name = "filterImage2",
    imageId = UUID.randomUUID
  )

  val entity2Updated: Generator = entity2.copy(imageId = UUID.randomUUID)

  val entity3 = Generator(
    id = UUID.randomUUID,
    name = "filterImage3",
    imageId = UUID.randomUUID
  )

}
