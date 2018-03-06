package de.htwg.zeta.common.model.style

case class Line(
    color: Color,
    style: LineStyle,
    transparent: Boolean,
    width: Int
)
object Line {
  val defaultColor: Color = Color("0,0,0")
  val defaultWidth: Int = 1
}

sealed trait LineStyle {
  def value: String
}

case class Dashed(value: String = "DASH") extends LineStyle

// TODO implement more style types