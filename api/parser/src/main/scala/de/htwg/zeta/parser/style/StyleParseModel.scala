package de.htwg.zeta.parser.style


sealed trait StyleAttribute {
  val attributeName: String
}
case class LineColor(color: String)                 extends StyleAttribute { val attributeName = "line-color" }
case class LineStyle(style: String)                 extends StyleAttribute { val attributeName = "line-style" }
case class LineWidth(width: Int)                    extends StyleAttribute { val attributeName = "line-width" }
case class Transparency(transparency: Double)       extends StyleAttribute { val attributeName = "transparency" }
case class BackgroundColor(color: String)           extends StyleAttribute { val attributeName = "background-color" }
case class FontColor(color: String)                 extends StyleAttribute { val attributeName = "font-color" }
case class FontName(name: String)                   extends StyleAttribute { val attributeName = "font-name" }
case class FontSize(size: Int)                      extends StyleAttribute { val attributeName = "font-size" }
case class FontBold(bold: String)                   extends StyleAttribute { val attributeName = "font-bold" }
case class FontItalic(italic: String)               extends StyleAttribute { val attributeName = "font-italic" }
case class GradientOrientation(orientation: String) extends StyleAttribute { val attributeName = "gradient-orientation" }
case class GradientAreaColor(color: String)         extends StyleAttribute { val attributeName = "gradient-area-color" }
case class GradientAreaOffset(offset: String)       extends StyleAttribute { val attributeName = "gradient-area-offset" }
// case class Allowed(allowed: Boolean)                extends StyleAttribute { val attributeName = "allowed" }
// case class UnAllowed(unAllowed: Boolean)            extends StyleAttribute { val attributeName = "unallowed" }
// case class Selected(selected: Boolean)              extends StyleAttribute { val attributeName = "selected" }
// case class MultiSelected(multiSelected: Boolean)    extends StyleAttribute { val attributeName = "multiselected" }
// case class Highlighting(highlighting: String)       extends StyleAttribute { val attributeName = "highlighting" }

case class StyleParseModel(
    name: String,
    description: String,
    parentStyles: List[String],
    attributes: List[StyleAttribute]
    )