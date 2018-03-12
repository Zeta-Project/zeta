package de.htwg.zeta.common.models.project.gdsl.style

case class Style(
    name: String,
    description: String,
    background: Background,
    font: Font,
    line: Line,
    transparency: Double
)
object Style {
  val defaultTransparency: Double = 1.0

  val defaultStyle: Style = Style(
    "default",
    "default",
    Background.defaultBackground,
    Font.defaultFont,
    Line.defaultLine,
    defaultTransparency
  )
}