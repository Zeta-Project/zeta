package de.htwg.zeta.common.models.project.gdsl.style

case class Line(
    color: Color,
    style: LineStyle,
    width: Int
)
object Line {
  val defaultColor: Color = Color.defaultColor
  val defaultStyle: LineStyle = Solid()
  val defaultWidth: Int = 1

  val defaultLine: Line = Line(
    defaultColor,
    defaultStyle,
    defaultWidth
  )
}

sealed trait LineStyle

case class Dotted() extends LineStyle
case class Solid() extends LineStyle
case class DoubleLine() extends LineStyle
case class Dashed() extends LineStyle
