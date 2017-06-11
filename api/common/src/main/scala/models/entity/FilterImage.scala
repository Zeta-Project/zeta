package models.entity

import java.util.UUID


case class FilterImage(
    id: UUID = UUID.randomUUID,
    name: String,
    dockerImage: String
) extends Entity
