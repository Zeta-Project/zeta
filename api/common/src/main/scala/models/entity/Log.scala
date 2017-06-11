package models.entity

import java.util.UUID


case class Log(
    id: UUID = UUID.randomUUID,
    task: String,
    log: String,
    status: Int,
    date: String
) extends Entity
