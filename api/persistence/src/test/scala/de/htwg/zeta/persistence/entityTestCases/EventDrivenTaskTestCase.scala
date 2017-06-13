package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.entity.EventDrivenTask


object EventDrivenTaskTestCase extends EntityTestCase[EventDrivenTask] {

  override val entity1 = EventDrivenTask(
    id = UUID.randomUUID,
    name = "eventDrivenTask1",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    event = "event1"
  )

  override val entity2 = EventDrivenTask(
    id = UUID.randomUUID,
    name = "eventDrivenTask2",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    event = "event2"
  )

  override val entity2Updated: EventDrivenTask = entity2.copy(filterId = UUID.randomUUID)

  override val entity3 = EventDrivenTask(
    id = UUID.randomUUID,
    name = "eventDrivenTask3",
    generatorId = UUID.randomUUID,
    filterId = UUID.randomUUID,
    event = "event3"
  )

}
