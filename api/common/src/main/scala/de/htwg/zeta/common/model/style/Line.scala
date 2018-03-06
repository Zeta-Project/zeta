package de.htwg.zeta.common.model.style

case class Line(
    color: Color,
    style: LineStyle,
    transparent: Boolean,
    width: Int
)
object Line {
  val defaultColor: Color = Color("0,0,0")
  val defaultStyle: LineStyle = Solid()
  val defaultTransparent: Boolean = false
  val defaultWidth: Int = 1
}

sealed trait LineStyle {
  def value: String
}

case class Dotted(value: String = "dotted") extends LineStyle
case class Solid(value: String = "solid") extends LineStyle
case class DoubleLine(value: String = "double") extends LineStyle
case class Dashed(value: String = "dash") extends LineStyle
