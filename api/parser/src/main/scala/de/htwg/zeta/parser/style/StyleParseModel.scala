package de.htwg.zeta.parser.style


sealed trait StyleAttribute {
  val attributeName: String
}
case class LineColor(color: String) extends StyleAttribute { val attributeName = "line-color" }
case class LineStyle(style: String) extends StyleAttribute { val attributeName = "line-style" }
case class LineWidth(width: Int)    extends StyleAttribute { val attributeName = "line-width" }


case class StyleParseModel(
    name: String,
    description: String,
    parentStyles: List[String],
    attributes: List[StyleAttribute]
    )