package de.htwg.zeta.common.models.entity

import java.util.UUID


case class Log(
    id: UUID,
    task: String,
    log: String,
    status: Int,
    date: String
) extends Entity
