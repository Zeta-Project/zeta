package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object ShapeParser extends CommonParserMethods with UniteParsers {

  def parseShapes(input: String): ParseResult[List[ShapeParseTree]] = {
    parseAll(shapes, input)
  }

  private def shapes = rep(node | edge)

  private def node = include(NodeParser.node)

  private def edge = include(EdgeParser.edge)

}
