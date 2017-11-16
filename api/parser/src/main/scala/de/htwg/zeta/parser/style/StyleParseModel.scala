package de.htwg.zeta.parser.style

case class StyleParseModel(
    name: String,
    description: Option[String] = None,
    lineColor: String,
    lineStyle: String,
    lineWidth: Int,
    gradientOrientation: String,
    backgroundColor: String,
    fontSize: Int)