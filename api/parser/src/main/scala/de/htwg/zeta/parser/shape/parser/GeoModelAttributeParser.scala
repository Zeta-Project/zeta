package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.EnumParser
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Align
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Curve
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Editable
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.For
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.HorizontalAlignment
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Multiline
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Point
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Text
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.VerticalAlignment

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
    "multiline" ~> colon ~> argumentBoolean ^^ {
      Multiline
    }
  }

  def style: Parser[Style] = {
    "style" ~> colon ~> ident ^^ {
      Style
    }
  }

  def position: Parser[Position] = parseWholeNumberTuple("position", "x", "y").map(Position.tupled)

  def point: Parser[Point] = parseWholeNumberTuple("point", "x", "y").map(Point.tupled)

  def size: Parser[Size] = parseNaturalNumberTuple("size", "width", "height").map(Size.tupled)

  def curve: Parser[Curve] = parseNaturalNumberTuple("curve", "width", "height").map(Curve.tupled)

  private def parseNaturalNumberTuple(name: String, arg1: String, arg2: String): Parser[(Int, Int)] = {
    (name ~> leftParenthesis ~> arg1 ~> colon ~> naturalNumber <~ comma) ~
      (arg2 ~> colon ~> naturalNumber <~ rightParenthesis) ^^ { tuple =>
      val first ~ second = tuple
      (first, second)
    }
  }

  private def parseWholeNumberTuple(name: String, arg1: String, arg2: String): Parser[(Int, Int)] = {
    (name ~> leftParenthesis ~> arg1 ~> colon ~> argumentInt <~ comma) ~
      (arg2 ~> colon ~> argumentInt <~ rightParenthesis) ^^ { tuple =>
      val first ~ second = tuple
      (first, second)
    }
  }

  def editable: Parser[Editable] = {
    "editable" ~> colon ~> argumentBoolean ^^ {
      Editable
    }
  }

  def foreach: Parser[For] = {
    ("for" ~> leftParenthesis ~> "each" ~> colon ~> ident <~ comma) ~
      ("as" ~> colon ~> ident <~ rightParenthesis) ^^ { result =>
      val each ~ as = result
      For(Identifier(each), as)
    }
  }

  def text: Parser[Text] = {
    "text" ~> colon ~> stringLiteral ^^ { string =>
      // string starts and ends with quotation marks -> drop them
      Text(string.drop(1).dropRight(1))
    }
  }
}
