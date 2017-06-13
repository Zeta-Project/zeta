package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.BondedTask


object BondedTaskTestCase extends EntityTestCase[BondedTask] {

  override val entity1 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask1",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu1",
    item = "item1"
  )

  override val entity2 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask2",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu2",
    item = "item2"
  )

  override val entity2Updated: BondedTask = entity2.copy(filterId = UUID.randomUUID)

  override val entity3 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask3",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu3",
    item = "item3"
  )

}
