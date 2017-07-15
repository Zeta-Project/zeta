package de.htwg.zeta.common.models.entity

import java.util.UUID


case class Filter(
    id: UUID,
    name: String,
    description: String,
    instanceIds: Seq[UUID],
    deleted: Option[Boolean] = Some(false)
) extends Entity
