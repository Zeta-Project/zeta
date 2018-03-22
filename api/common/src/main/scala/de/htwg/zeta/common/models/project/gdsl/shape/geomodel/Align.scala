package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Horizontal.HorizontalAlignment
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align.Vertical.VerticalAlignment

case class Align(
    horizontal: HorizontalAlignment,
    vertical: VerticalAlignment
)

object Align {
  val default: Align = Align(Horizontal.middle, Vertical.middle)

  object Horizontal extends Enumeration {
    type HorizontalAlignment = Value
    val left, middle, right = Value
  }

  object Vertical extends Enumeration {
    type VerticalAlignment = Value
    val top, middle, bottom = Value
  }
}
