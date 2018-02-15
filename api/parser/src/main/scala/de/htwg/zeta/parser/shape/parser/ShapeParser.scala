package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object ShapeParser extends CommonParserMethods with UniteParsers {

  private def node = include(NodeParser.parseNode)

  def parseShapes(input: String): ParseResult[List[ShapeParseTree]] = {
    parse(shapes, input)
  }

  private def shapes = rep(node)

}
