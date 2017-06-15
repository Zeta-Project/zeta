package de.htwg.zeta.common.models.entity

import java.util.UUID


case class FilterImage(
    id: UUID,
    name: String,
    dockerImage: String
) extends Entity
