package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.Filter


object FilterTestCase extends EntityTestCase[Filter] {

  override val entity1 = Filter(
    id = UUID.randomUUID,
    name = "filter1",
    description = "description1",
    instanceIds = Seq(UUID.randomUUID, UUID.randomUUID, UUID.randomUUID)
  )

  override val entity2 = Filter(
    id = UUID.randomUUID,
    name = "filter2",
    description = "description2",
    instanceIds = Seq(UUID.randomUUID, UUID.randomUUID)
  )

  override val entity2Updated: Filter = entity2.copy(instanceIds = Seq(UUID.randomUUID))

  override val entity3 = Filter(
    id = UUID.randomUUID,
    name = "filter3",
    description = "description3",
    instanceIds = Seq.empty
  )

}
