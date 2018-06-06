package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.common.CommentParser
import de.htwg.zeta.parser.common.ParseError
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

object ShapeParser extends CommonParserMethods with UniteParsers {

  def parseShapes(input: String): Either[ParseError, List[ShapeParseTree]] = {
    val strippedResult = CommentParser().parseComments(input)
    strippedResult match {
      case Left(l) => Left(l)
      case Right(t) =>
        parseAll(shapes, t.text) match {
          case NoSuccess(msg, next) => Left(t.recalculatePosition(ParseError(msg, next.offset, (next.pos.line, next.pos.column))))
          case Success(s, _) => Right(s)
        }
    }
  }

  private def shapes = rep(node | edge)

  private def node = include(NodeParser.node)

  private def edge = include(EdgeParser.edge)

}
