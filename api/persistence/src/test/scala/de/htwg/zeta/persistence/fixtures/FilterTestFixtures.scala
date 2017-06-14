package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.Filter


object FilterTestFixtures {

  val entity1 = Filter(
    id = UUID.randomUUID,
    name = "filter1",
    description = "description1",
    instanceIds = Seq(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID)
  )

  val entity2 = Filter(
    id = UUID.randomUUID,
    name = "filter2",
    description = "description2",
    instanceIds = Seq(UUID.randomUUID, UUID.randomUUID)
  )

  val entity2Updated: Filter = entity2.copy(instanceIds = Seq(UUID.randomUUID))

  val entity3 = Filter(
    id = UUID.randomUUID,
    name = "filter3",
    description = "description3",
    instanceIds = Seq.empty
  )

}
