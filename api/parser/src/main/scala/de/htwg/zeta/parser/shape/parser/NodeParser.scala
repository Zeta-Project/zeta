package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.shape.parsetree.Attributes.Attribute
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.{Collector, UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object NodeParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def parseNode: Parser[NodeParseTree] = {
    val attributes = unordered(once(style), once(sizeMin), once(sizeMax), optional(resizing))
    ("node" ~> ident) ~ ("for" ~> ident) ~ leftBrace ~ edges ~ attributes ~ geoModels ~ rightBrace ^^ { parseResult =>
      val nodeName ~ conceptElement ~ _ ~ edges ~ attributes ~ geoModels ~ _ = parseResult
      val attrs = Collector(attributes)
      NodeParseTree(nodeName,
        conceptElement,
        edges,
        attrs.*[Attribute],
        geoModels)
    }
  }

  private def edges: Parser[List[String]] = {
    opt("edges" ~> leftBrace ~> rep(ident) <~ rightBrace) ^^ {
      case Some(edges) => edges
      case None => List()
    }
  }

  private def resizing = include(AttributeParser.resizing)

  private def style = include(AttributeParser.style)

  private def sizeMin = include(AttributeParser.sizeMin)

  private def sizeMax = include(AttributeParser.sizeMax)

  private def geoModels = include(GeoModelParser.geoModels)

}
