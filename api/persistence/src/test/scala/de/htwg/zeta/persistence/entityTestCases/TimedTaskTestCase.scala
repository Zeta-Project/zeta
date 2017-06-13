package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.TimedTask


object TimedTaskTestCase extends EntityTestCase[TimedTask] {

  override val entity1 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask1",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 1,
    start = "start1"
  )

  override val entity2 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask2",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 2,
    start = "start2"
  )

  override val entity2Updated: TimedTask = entity2.copy(start = "start2Updated")

  override val entity3 = TimedTask(
    id = UUID.randomUUID,
    name = "timedTask3",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    interval = 3,
    start = "start3"
  )

}
