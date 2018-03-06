package de.htwg.zeta.common.model.style

case class Font(
    name: String,
    bold: Boolean,
    color: Color,
    italic: Boolean,
    size: Int,
    transparent: Boolean
)
object Font {
  val defaultBold: Boolean = false
  val defaultColor: Color = Color(0, 0, 0)
  val defaultItalic: Boolean = false
  val defaultName: String = "Arial"
  val defaultSize: Int = 10
  val defaultTransparent: Boolean = false
}