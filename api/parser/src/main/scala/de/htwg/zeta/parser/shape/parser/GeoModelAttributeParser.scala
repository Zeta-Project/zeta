package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelAttributeParser extends CommonParserMethods with UniteParsers {

  def align: Parser[Align] = {
    ("align" ~> leftParenthesis ~> "horizontal" ~> colon ~> horizontal <~ comma) ~
      ("vertical" ~> colon ~> vertical <~ rightParenthesis) ^^ { result =>
      val horizontal ~ vertical = result
      Align(horizontal, vertical)
    }
  }

  private def horizontal = {
    ("left" | "middle" | "right") ^^ {
      HorizontalAlignment.withName
    }
  }

  private def vertical = {
    ("top" | "middle" | "bottom") ^^ {
      VerticalAlignment.withName
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

  def curvedPoint: Parser[CurvedPoint] = {
    ("point" ~> leftParenthesis ~> "x" ~> colon ~> natural_number <~ comma) ~
      ("y" ~> colon ~> natural_number <~ comma) ~
      ("curveBefore" ~> colon ~> natural_number <~ comma) ~
      ("curveAfter" ~> colon ~> natural_number <~ rightParenthesis) ^^ { result =>
      val x ~ y ~ curvedBefore ~ curvedAfter = result
      CurvedPoint(x, y, curvedBefore, curvedAfter)
    }
  }

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
}
