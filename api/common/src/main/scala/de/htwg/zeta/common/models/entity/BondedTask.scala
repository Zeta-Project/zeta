package de.htwg.zeta.common.models.entity

import java.util.UUID


case class BondedTask(
    id: UUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    menu: String,
    item: String
) extends Entity
