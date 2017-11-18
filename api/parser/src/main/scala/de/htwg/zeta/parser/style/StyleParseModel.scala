package de.htwg.zeta.parser.style


sealed trait StyleAttribute
case class LineColor(color: String) extends StyleAttribute
case class LineStyle(style: String) extends StyleAttribute
case class LineWidth(width: Int) extends StyleAttribute


case class StyleParseModel(
    name: String,
    description: String,
    parentStyles: List[String],
    attributes: List[StyleAttribute]
    )