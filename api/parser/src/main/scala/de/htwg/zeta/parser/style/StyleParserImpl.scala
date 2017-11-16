package de.htwg.zeta.parser.style

class StyleParserImpl extends StyleParser {

  private val leftBraces  = literal("{")
  private val rightBraces = literal("}")
  private val eq = "="



  override protected def style: Parser[StyleParseModel] = {
    name ~ (leftBraces ~> description) ~ lineColor ~ lineStyle ~ lineWidth ~
      gradientOrientation ~ backgroundColor ~ fontSize <~ rightBraces ^^ {
      case name ~ description ~ lineColor ~ lineStyle ~ lineWidth ~
            gradientOrientation ~ backgroundColor ~ fontSize =>
        StyleParseModel(name,
              description,
              lineColor,
              lineStyle,
              lineWidth,
              gradientOrientation,
              backgroundColor,
              fontSize)
    }

  }

  private def name = literal("style") ~> ident
  private def description = opt(literal("description") ~ eq ~> argument_string)
  private def lineColor = literal("line-color") ~ eq ~> argument
  private def lineStyle = literal("line-style") ~ eq ~> argument
  private def lineWidth = literal("line-width") ~ eq ~> argument_int
  private def gradientOrientation = literal("gradient-orientation") ~ eq ~> argument
  private def backgroundColor = literal("background-color") ~ eq ~> argument
  private def fontSize = literal("font-size") ~ eq ~> argument_int
}

