package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.GeneratorImage


object GeneratorImageTestCase extends EntityTestCase[GeneratorImage] {

  override val entity1 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage1",
    dockerImage = "dockerImage1"
  )

  override val entity2 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage2",
    dockerImage = "dockerImage2"
  )

  override val entity2Updated: GeneratorImage = entity2.copy(name = "filterImage2Updated")

  override val entity3 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage3",
    dockerImage = "dockerImage3"
  )

}
