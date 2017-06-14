package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.TimedTask


object TimedTaskFixtures {

  val entity1 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask1",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 1,
    start = "start1"
  )

  val entity2 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask2",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 2,
    start = "start2"
  )

  val entity2Updated: TimedTask = entity2.copy(start = "start2Updated")

  val entity3 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask3",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 3,
    start = "start3"
  )

}
