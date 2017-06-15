package models.entity

import java.util.UUID


case class EventDrivenTask(
    id: UUID = UUID.randomUUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    event: String
) extends Entity
