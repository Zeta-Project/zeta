package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.FilterImage


object FilterImageTestCase extends EntityTestCase[FilterImage] {

  override val entity1 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage1",
    dockerImage = "dockerImage1"
  )

  override val entity2 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage2",
    dockerImage = "dockerImage2"
  )

  override val entity2Updated: FilterImage = entity2.copy(dockerImage = "dockerImage2Updated")

  override val entity3 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage3",
    dockerImage = "dockerImage3"
  )

}
