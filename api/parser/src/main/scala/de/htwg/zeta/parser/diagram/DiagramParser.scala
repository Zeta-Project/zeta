package de.htwg.zeta.parser.diagram

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.common.CommentParser
import de.htwg.zeta.parser.common.ParseError

object DiagramParser extends CommonParserMethods {

  def parseDiagrams(input: String): Either[ParseError, List[DiagramParseTree]] = {
    val strippedResult = CommentParser().parseComments(input)
    strippedResult match {
      case Left(l) => Left(l)
      case Right(t) =>
        parseAll(diagrams, t.text) match {
          case NoSuccess(msg, next) => Left(t.recalculatePosition(ParseError(msg, next.offset, (next.pos.line, next.pos.column))))
          case Success(s, _) => Right(s)
        }
    }
  }

  private def diagrams: Parser[List[DiagramParseTree]] = rep(diagram)

  private def diagram: Parser[DiagramParseTree] = {
    diagramName ~ leftBrace ~ palettes ~ rightBrace ^^ { parseResult =>
      val diagramName ~ _ ~ palettes ~ _ = parseResult
      DiagramParseTree(diagramName, palettes)
    }
  }

  private def palettes: Parser[List[PaletteParseTree]] = rep(palette)

  private def palette: Parser[PaletteParseTree] = {
    paletteName ~ leftBrace ~ nodes ~ rightBrace ^^ { parseResult =>
      val paletteName ~ _ ~ nodes ~ _ = parseResult
      PaletteParseTree(paletteName, nodes)
    }
  }

  private def nodes: Parser[List[NodeParseTree]] = rep(node)

  private def node: Parser[NodeParseTree] = ident ^^ NodeParseTree

  private def diagramName: Parser[String] = literal("diagram") ~> ident

  private def paletteName: Parser[String] = literal("palette") ~> ident

}
