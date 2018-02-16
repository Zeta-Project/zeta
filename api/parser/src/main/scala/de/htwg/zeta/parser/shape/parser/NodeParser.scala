package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.{Collector, UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object NodeParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def parseNode: Parser[NodeParseTree] = {
    val attributes = unordered(once(sizeMin), once(sizeMax), optional(style), optional(resizing), arbitrary(anchor))
    ("node" ~> ident) ~ ("for" ~> ident) ~ leftBrace ~ edges ~ attributes ~ geoModels ~ rightBrace ^^ { parseResult =>
      val nodeName ~ conceptElement ~ _ ~ edges ~ attributes ~ geoModels ~ _ = parseResult
      val attrs = Collector(attributes)
      NodeParseTree(
        nodeName,
        conceptElement,
        edges,
        attrs.![SizeMin],
        attrs.![SizeMax],
        attrs.?[NodeStyle],
        attrs.?[Resizing],
        attrs.*[Anchor],
        geoModels
      )
    }
  }

  private def edges: Parser[List[String]] = {
    opt("edges" ~> leftBrace ~> rep(ident) <~ rightBrace) ^^ {
      case Some(edges) => edges
      case None => List()
    }
  }

  private def resizing = include(NodeAttributeParser.resizing)

  private def style = include(NodeAttributeParser.style)

  private def sizeMin = include(NodeAttributeParser.sizeMin)

  private def sizeMax = include(NodeAttributeParser.sizeMax)

  private def anchor = include(NodeAttributeParser.anchor)

  private def geoModels = include(GeoModelParser.geoModels)

}
