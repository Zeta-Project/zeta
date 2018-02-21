package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.{EnumParser, UniteParsers}
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelAttributeParser extends CommonParserMethods with UniteParsers {

  def align: Parser[Align] = {
    val horizontal = include(EnumParser.parseEnum(HorizontalAlignment))
    val vertical = include(EnumParser.parseEnum(VerticalAlignment))
    ("align" ~> leftParenthesis ~> "horizontal" ~> colon ~> horizontal <~ comma) ~
      ("vertical" ~> colon ~> vertical <~ rightParenthesis) ^^ { result =>
      val horizontal ~ vertical = result
      Align(horizontal, vertical)
    }
  }

  def identifier: Parser[Identifier] = {
    "identifier" ~> colon ~> ident ^^ {
      Identifier
    }
  }

  def multiline: Parser[Multiline] = {
    "multiline" ~> colon ~> argument_boolean ^^ {
      Multiline
    }
  }

  def style: Parser[Style] = {
    "style" ~> colon ~> ident ^^ {
      Style
    }
  }

  def position: Parser[Position] = parseNaturalNumberTuple("position", "x", "y").map(Position.tupled)

  def point: Parser[Point] = parseNaturalNumberTuple("point", "x", "y").map(Point.tupled)

  def size: Parser[Size] = parseNaturalNumberTuple("size", "width", "height").map(Size.tupled)

  def curve: Parser[Curve] = parseNaturalNumberTuple("curve", "width", "height").map(Curve.tupled)

  private def parseNaturalNumberTuple(name: String, arg1: String, arg2: String): Parser[(Int, Int)] = {
    (name ~> leftParenthesis ~> arg1 ~> colon ~> natural_number <~ comma) ~
      (arg2 ~> colon ~> natural_number <~ rightParenthesis) ^^ { tuple =>
      val first ~ second = tuple
      (first, second)
    }
  }

  def editable: Parser[Editable] = {
    "editable" ~> colon ~> argument_boolean ^^ {
      Editable
    }
  }

  def foreach: Parser[For] = {
    ("for" ~> leftParenthesis ~> "each" ~> colon ~> ident <~ comma) ~
      ("as" ~> colon ~> ident <~ rightParenthesis) ^^ { result =>
      val each ~ as = result
      For(each, as)
    }
  }

  def text: Parser[Text] = {
    "text" ~> colon ~> stringLiteral ^^ { string =>
      // string starts and ends with quotation marks -> drop them
      Text(string.drop(1).dropRight(1))
    }
  }
}
