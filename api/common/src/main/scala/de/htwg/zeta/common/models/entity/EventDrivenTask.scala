package de.htwg.zeta.common.models.entity

import java.util.UUID


case class EventDrivenTask(
    id: UUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    event: String
) extends Entity
