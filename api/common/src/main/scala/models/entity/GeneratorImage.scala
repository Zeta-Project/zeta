package models.entity

import java.util.UUID


case class GeneratorImage(
    id: UUID = UUID.randomUUID,
    name: String,
    dockerImage: String
) extends Entity
