package de.htwg.zeta.common.model.style

case class Line(
    color: Color,
    style: LineStyle,
    transparent: Boolean,
    width: Int
)
object Line {
  val defaultColor: Color = Color(0, 0, 0)
  val defaultStyle: LineStyle = Solid()
  val defaultTransparent: Boolean = false
  val defaultWidth: Int = 1
}

sealed trait LineStyle

case class Dotted() extends LineStyle
case class Solid() extends LineStyle
case class DoubleLine() extends LineStyle
case class Dashed() extends LineStyle
