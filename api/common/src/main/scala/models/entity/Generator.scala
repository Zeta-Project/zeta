package models.entity

import java.util.UUID


case class Generator(
    id: UUID = UUID.randomUUID,
    name: String,
    imageId: UUID
) extends Entity
