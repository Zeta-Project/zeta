package de.htwg.zeta.parser.style

import javafx.scene.paint.Color

sealed abstract class StyleAttribute(val attributeName: String)

object StyleAttribute {

  // style dsl attribute names
  val backgroundColor = "background-color"
  val fontBold = "font-bold"
  val fontColor = "font-color"
  val fontItalic = "font-italic"
  val fontName = "font-name"
  val fontSize = "font-size"
  val gradientAreaColor = "gradient-area-color"
  val gradientAreaOffset = "gradient-area-offset"
  val gradientOrientation = "gradient-orientation"
  val lineColor = "line-color"
  val lineStyle = "line-style"
  val lineWidth = "line-width"
  val transparency = "transparency"

}

case class BackgroundColor(color: Color)            extends StyleAttribute(StyleAttribute.backgroundColor)
case class FontBold(bold: Boolean)                  extends StyleAttribute(StyleAttribute.fontBold)
case class FontColor(color: Color)                  extends StyleAttribute(StyleAttribute.fontColor)
case class FontItalic(italic: Boolean)              extends StyleAttribute(StyleAttribute.fontItalic)
case class FontName(name: String)                   extends StyleAttribute(StyleAttribute.fontName)
case class FontSize(size: Int)                      extends StyleAttribute(StyleAttribute.fontSize)
case class GradientAreaColor(color: Color)          extends StyleAttribute(StyleAttribute.gradientAreaColor)
case class GradientAreaOffset(offset: Double)       extends StyleAttribute(StyleAttribute.gradientAreaOffset)
case class GradientOrientation(orientation: String) extends StyleAttribute(StyleAttribute.gradientOrientation)
case class LineColor(color: Color)                  extends StyleAttribute(StyleAttribute.lineColor)
case class LineStyle(style: String)                 extends StyleAttribute(StyleAttribute.lineStyle)
case class LineWidth(width: Int)                    extends StyleAttribute(StyleAttribute.lineWidth)
case class Transparency(transparency: Double)       extends StyleAttribute(StyleAttribute.transparency)

case class StyleParseTree(
  name: String,
  description: String,
  parentStyles: List[String],
  attributes: List[StyleAttribute]
  )
