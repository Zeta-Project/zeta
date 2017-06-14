package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.BondedTask


object BondedTaskFixtures {

  val entity1 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask1",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu1",
    item = "item1"
  )

  val entity2 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask2",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu2",
    item = "item2"
  )

  val entity2Updated: BondedTask = entity2.copy(filterId = UUID.randomUUID)

  val entity3 = BondedTask(
    id = UUID.randomUUID,
    name = "bondedTask3",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    menu = "menu3",
    item = "item3"
  )

}
