package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.Collector
import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.UnorderedParser
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.EdgeStyle
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Offset
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree

object EdgeParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def edge: Parser[EdgeParseTree] = {
    val attributes = unordered(once(target), optional(edgeStyle), min(1, placing))
    ("edge" ~> ident) ~ ("for" ~> conceptConnection) ~ (leftBrace ~> attributes <~ rightBrace) ^^ { parseResult =>
      val edge ~ conceptConnection ~ attributes = parseResult
      val attrs = Collector(attributes)
      EdgeParseTree(edge,
        conceptConnection,
        attrs.![Target],
        attrs.?[EdgeStyle],
        attrs.*[Placing])
    }
  }

  private val styleLiteral = "style"
  private val targetLiteral = "target"
  private val placingLiteral = "placing"
  private val offsetLiteral = "offset"

  private def edgeStyle: Parser[EdgeStyle] = {
    styleLiteral ~! colon ~ ident ^^ {
      case _ ~ _ ~ name => EdgeStyle(name)
    }
  }.named(styleLiteral)

  private def target: Parser[Target] = {
    targetLiteral ~! colon ~ ident ^^ {
      case _ ~ _ ~ name => Target(name)
    }
  }.named(targetLiteral)

  private def placing: Parser[Placing] = {
    val attributes = unordered(optional(style), once(geoModel), once(offset))
    placingLiteral ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      Placing(
        attrs.?[Style],
        attrs.![Offset],
        attrs.![GeoModelParseTree]
      )
    }
  }.named(placingLiteral)

  private def offset: Parser[Offset] = {
    literal(offsetLiteral) ~! colon ~ argumentDouble ^^ {
      case _ ~ _ ~ offset => Offset(offset)
    }
  }.named(offsetLiteral)

  private def style = include(GeoModelAttributeParser.style).named(styleLiteral)

  private def geoModel = include(GeoModelParser.geoModel).named("geoModel")

  // Concept connections can be addressed with java / scala object syntax
  // e.g.: Knoten.hatKind
  private def conceptConnection = regex("\\w+.\\w+".r)

}
