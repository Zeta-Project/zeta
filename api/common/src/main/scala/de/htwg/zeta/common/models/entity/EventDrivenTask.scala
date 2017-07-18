package de.htwg.zeta.common.models.entity

import java.util.UUID


case class EventDrivenTask(
    id: UUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    event: String,
    deleted: Option[Boolean] = Some(false)
) extends Entity
