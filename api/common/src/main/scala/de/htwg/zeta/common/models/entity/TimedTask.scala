package de.htwg.zeta.common.models.entity

import java.time.LocalDateTime
import java.time.Duration
import java.util.UUID

case class TimedTask(
    id: UUID,
    name: String,
    generatorId: UUID,
    filterId: UUID,
    interval: Int,
    start: String,
    deleted: Boolean = false
) extends Entity {

  def delay: Int = {
    val time = Duration.between(LocalDateTime.parse(start), LocalDateTime.now()).toMinutes

    // at least 1 minute, but a maximum of Int.MaxValue minutes.
    Math.max(1, Math.min(Int.MaxValue.toLong, time).toInt)
  }

}
