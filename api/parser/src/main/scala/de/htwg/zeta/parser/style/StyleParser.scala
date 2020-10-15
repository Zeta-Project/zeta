package de.htwg.zeta.parser.style

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.common.CommentParser
import de.htwg.zeta.parser.common.ParseError

object StyleParser extends CommonParserMethods with UniteParsers {

  def parseStyles(input: String): Either[ParseError, List[StyleParseTree]] = {
    val strippedResult = CommentParser().parseComments(input)
    strippedResult match {
      case Left(l) => Left(l)
      case Right(t) =>
        parseAll(styles, t.text) match {
          case NoSuccess(msg, next) =>
            val newPosition = t.recalculatePosition(ParseError(msg, next.offset, (next.pos.line, next.pos.column)))
            Left(newPosition)
          case Success(s, _) => Right(s.styleParserTree)
        }
    }
  }

  private def styles: Parser[MainStyleParserTree] = positioned {
    phrase(rep(style)) ^^ (arg => MainStyleParserTree(arg))
  }

  private def style: Parser[StyleParseTree] = positioned {
    name ~ opt(parentStyles) ~ styleAttributes ^^ { parseSeq =>
      val name ~ parentStyles ~ styleAttributes = parseSeq
      StyleParseTree(
        name,
        styleAttributes.description,
        parentStyles.getOrElse(List()),
        styleAttributes.attributes
      )
    }
  }

  private def styleAttributes: Parser[StyleAttributes] = positioned {
    leftBrace ~> description ~ attributes <~ rightBrace ^^ {
      case description ~ (attributes: List[StyleAttribute]) =>  new StyleAttributes(description, attributes)
    }
  }

  private def attributes: Parser[List[StyleAttribute]] = {
    rep(lineColor | lineStyle | lineWidth | transparency | backgroundColor | fontColor | fontName | fontSize
      | fontBold | fontItalic | gradientOrientation | gradientAreaColor | gradientAreaOffset)
  }

  private def name = literal("style") ~> ident

  private def description: Parser[StyleDescription] = positioned("description" ~ eq ~> argumentString ^^ (arg => StyleDescription(arg)))

  private def lineColor = positioned("line-color" ~ eq ~> argumentColor ^^ (arg => LineColor(arg)))

  private def lineStyle = positioned("line-style" ~ eq ~> argument ^^ (arg => LineStyle(arg)))

  private def lineWidth = positioned("line-width" ~ eq ~> argumentInt ^^ (arg => LineWidth(arg)))

  private def transparency = positioned("transparency" ~ eq ~> argumentDouble ^^ (arg => Transparency(arg)))

  private def backgroundColor = positioned("background-color" ~ eq ~> argumentColor ^^ (arg => BackgroundColor(arg)))

  private def fontColor = positioned("font-color" ~ eq ~> argumentColor ^^ (arg => FontColor(arg)))

  private def fontName = positioned("font-name" ~ eq ~> argument ^^ (arg => FontName(arg)))

  private def fontSize = positioned("font-size" ~ eq ~> argumentInt ^^ (arg => FontSize(arg)))

  private def fontBold = positioned("font-bold" ~ eq ~> argumentBoolean ^^ (arg => FontBold(arg)))

  private def fontItalic = positioned("font-italic" ~ eq ~> argumentBoolean ^^ (arg => FontItalic(arg)))

  private def gradientOrientation =
    positioned("gradient-orientation" ~ eq ~> (GradientOrientation.vertical | GradientOrientation.horizontal) ^^ (arg => GradientOrientation(arg)))

  private def gradientAreaColor = positioned("gradient-area-color" ~ eq ~> argumentColor ^^ (arg => GradientAreaColor(arg)))

  private def gradientAreaOffset = positioned("gradient-area-offset" ~ eq ~> argumentDouble ^^ (arg => GradientAreaOffset(arg)))

  private def parentStyles = literal("extends") ~> ident ~ rep(comma ~> ident) ^^ (parents => parents._1 :: parents._2)
}
