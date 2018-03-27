package de.htwg.zeta.common.models.project.gdsl.style

case class Font(
    name: String,
    bold: Boolean,
    color: Color,
    italic: Boolean,
    size: Int
)
object Font {
  val defaultBold: Boolean = false
  val defaultColor: Color = Color.defaultColor
  val defaultItalic: Boolean = false
  val defaultName: String = "Arial"
  val defaultSize: Int = 10

  val defaultFont: Font = Font(
    defaultName,
    defaultBold,
    defaultColor,
    defaultItalic,
    defaultSize
  )
}