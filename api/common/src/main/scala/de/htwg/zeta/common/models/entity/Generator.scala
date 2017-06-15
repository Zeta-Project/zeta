package de.htwg.zeta.common.models.entity

import java.util.UUID


case class Generator(
    id: UUID,
    name: String,
    imageId: UUID
) extends Entity
