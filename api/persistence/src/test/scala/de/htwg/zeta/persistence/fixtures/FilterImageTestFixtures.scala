package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.FilterImage


object FilterImageTestFixtures {

  val entity1 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage1",
    dockerImage = "dockerImage1"
  )

  val entity2 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage2",
    dockerImage = "dockerImage2"
  )

  val entity2Updated: FilterImage = entity2.copy(dockerImage = "dockerImage2Updated")

  val entity3 = FilterImage(
    id = UUID.randomUUID,
    name = "filterImage3",
    dockerImage = "dockerImage3"
  )

}
