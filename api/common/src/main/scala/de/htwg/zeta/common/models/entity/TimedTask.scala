package de.htwg.zeta.common.models.entity

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.Minutes

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
    val first = DateTime.parse(start)
    val now = new DateTime()
    val diff = Minutes.minutesBetween(now, first).getMinutes
    if (diff > 0) diff else 1
  }

}
