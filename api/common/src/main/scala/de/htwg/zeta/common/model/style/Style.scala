package de.htwg.zeta.common.model.style

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
}