package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.Generator


object GeneratorTestCase extends EntityTestCase[Generator] {

  override val entity1 = Generator(
    id = UUID.randomUUID,
    name = "filterImage1",
    imageId = UUID.randomUUID
  )

  override val entity2 = Generator(
    id = UUID.randomUUID,
    name = "filterImage2",
    imageId = UUID.randomUUID
  )

  override val entity2Updated: Generator = entity2.copy(imageId = UUID.randomUUID)

  override val entity3 = Generator(
    id = UUID.randomUUID,
    name = "filterImage3",
    imageId = UUID.randomUUID
  )

}
