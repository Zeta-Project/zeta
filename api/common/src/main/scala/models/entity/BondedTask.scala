package models.entity

import java.util.UUID


case class BondedTask(
    id: UUID = UUID.randomUUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    menu: String,
    item: String
) extends Entity
