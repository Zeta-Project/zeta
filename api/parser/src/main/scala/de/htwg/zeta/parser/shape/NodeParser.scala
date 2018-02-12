package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.NodeAttributes.{NodeAttribute, SizeMax, SizeMin, Style}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object NodeParser extends CommonParserMethods with UniteParsers {

  private val leftBrace = "{"
  private val rightBrace = "}"
  private val colon = ":"
  private val leftParenthesis = "("
  private val rightParenthesis = ")"
  private val comma = ","

  def parseNode: Parser[NodeParseTree] = {
    name ~ conceptElement ~ leftBrace ~ edges ~ attributes ~ rightBrace ^^ { parseResult =>
      val diagramName ~ conceptElement ~ _ ~ edges ~ attributes ~ _ = parseResult
      NodeParseTree(diagramName, conceptElement, edges, attributes, List())
    }
  }

  private def name: Parser[String] = literal("node") ~> ident

  private def conceptElement: Parser[String] = literal("for") ~> ident

  private def edges: Parser[List[String]] = {
    opt("edges" ~> leftBrace ~> rep(ident) <~ rightBrace) ^^ {
      case Some(edges) => edges
      case None => List()
    }
  }

  private def attributes: Parser[List[NodeAttribute]] = rep(style | sizeMin | sizeMax)

  private def style: Parser[Style] = "style" ~> colon ~> ident ^^ { style => Style(style) }

  private def sizeMin: Parser[SizeMin] = "sizeMin" ~> parseSize ^^ {
    case (width, height) => SizeMin(width, height)
  }

  private def sizeMax: Parser[SizeMax] = "sizeMax" ~> parseSize ^^ {
    case (width, height) => SizeMax(width, height)
  }

  // TODO style allows parsing of negative sizes and stuff
  private def parseSize: Parser[(Int, Int)] = leftParenthesis ~ "width" ~ colon ~ natural_number ~ comma ~ "height" ~ colon ~ natural_number ~ rightParenthesis ^^ { parseResult =>
    val _ ~ _ ~ _ ~ width ~ _ ~ _ ~ _ ~ height ~ _ = parseResult
    (width, height)
  }
}
