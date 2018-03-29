package de.htwg.zeta.parser.style

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers

object StyleParser extends CommonParserMethods with UniteParsers {

  def parseStyles(input: String): ParseResult[List[StyleParseTree]] = parseAll(styles, trimRight(input))

  private def styles: Parser[List[StyleParseTree]] = rep(style)

  private def style: Parser[StyleParseTree] = {
    name ~ opt(parentStyles) ~ leftBrace ~ description ~ attributes ~ rightBrace ^^ { parseSeq =>
      val name ~ parentStyles ~ _ ~ description ~ (attributes: List[StyleAttribute]) ~ _ = parseSeq
      StyleParseTree(
        name,
        description,
        parentStyles.getOrElse(List()),
        attributes
      )
    }
  }

  private def attributes: Parser[List[StyleAttribute]] = {
    rep(lineColor | lineStyle | lineWidth | transparency | backgroundColor | fontColor | fontName | fontSize
      | fontBold | fontItalic | gradientOrientation | gradientAreaColor | gradientAreaOffset)
  }

  private def name = literal("style") ~> ident

  private def description = literal("description") ~ eq ~> argumentString

  private def lineColor = literal("line-color") ~ eq ~> argumentColor ^^ (arg => LineColor(arg))

  private def lineStyle = literal("line-style") ~ eq ~> argument ^^ (arg => LineStyle(arg))

  private def lineWidth = literal("line-width") ~ eq ~> argumentInt ^^ (arg => LineWidth(arg))

  private def transparency = literal("transparency") ~ eq ~> argumentDouble ^^ (arg => Transparency(arg))

  private def backgroundColor = literal("background-color") ~ eq ~> argumentColor ^^ (arg => BackgroundColor(arg))

  private def fontColor = literal("font-color") ~ eq ~> argumentColor ^^ (arg => FontColor(arg))

  private def fontName = literal("font-name") ~ eq ~> argument ^^ (arg => FontName(arg))

  private def fontSize = literal("font-size") ~ eq ~> argumentInt ^^ (arg => FontSize(arg))

  private def fontBold = literal("font-bold") ~ eq ~> argumentBoolean ^^ (arg => FontBold(arg))

  private def fontItalic = literal("font-italic") ~ eq ~> argumentBoolean ^^ (arg => FontItalic(arg))

  private def gradientOrientation =
    literal("gradient-orientation") ~ eq ~> (GradientOrientation.vertical | GradientOrientation.horizontal) ^^ (arg => GradientOrientation(arg))

  private def gradientAreaColor = literal("gradient-area-color") ~ eq ~> argumentColor ^^ (arg => GradientAreaColor(arg))

  private def gradientAreaOffset = literal("gradient-area-offset") ~ eq ~> argumentDouble ^^ (arg => GradientAreaOffset(arg))

  private def parentStyles = literal("extends") ~> ident ~ rep(comma ~> ident) ^^ (parents => parents._1 :: parents._2)
}
