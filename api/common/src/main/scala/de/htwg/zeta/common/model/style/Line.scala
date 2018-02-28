package de.htwg.zeta.common.model.style

case class Line(
    color: Color,
    style: LineStyle,
    transparent: Boolean,
    width: Int
)

sealed trait LineStyle {
  def value: String
}

case class Dashed(value: String = "DASH") extends LineStyle

// TODO implement more style types