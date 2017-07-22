package de.htwg.zeta.common.models.entity

import java.util.UUID


case class Filter(
    id: UUID,
    name: String,
    description: String,
    instanceIds: Seq[UUID],
    files: Map[UUID, String] = Map(),
    deleted: Boolean = false
) extends Entity
