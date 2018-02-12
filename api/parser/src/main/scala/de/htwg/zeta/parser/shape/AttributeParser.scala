package de.htwg.zeta.parser.shape


import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.shape.Attributes.HorizontalAlignment.HorizontalAlignment
import de.htwg.zeta.parser.shape.Attributes._
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object AttributeParser extends CommonParserMethods with UniteParsers with ShapeTokens {

  private val lp = leftParenthesis
  private val rp = rightParenthesis

  def align: Parser[Align] =
    ("align" ~> lp ~> "horizontal" ~> colon ~> horizontal <~ comma) ~ ("vertical" ~> colon ~> vertical <~ rp) ^^ { result =>
      val horizontal ~ vertical = result
      Align(horizontal, vertical)
    }

  private def horizontal = ("left" | "middle" | "right") ^^ { alignment => HorizontalAlignment.withName(alignment) }

  private def vertical = ("top" | "middle" | "bottom") ^^ { alignment => VerticalAlignment.withName(alignment) }

  def resizing: Parser[Resizing] =
    ("resizing" ~> lp ~> "horizontal" ~> colon ~> argument_boolean <~ comma) ~
      ("vertical" ~> colon ~> argument_boolean <~ comma) ~
      ("proportional" ~> colon ~> argument_boolean <~ rp) ^^ { result =>
      val horizontal ~ vertical ~ proportional = result
      Resizing(horizontal, vertical, proportional)
    }

  def identifier: Parser[String] = "identifier" ~> colon ~> ident

  def multiline: Parser[Boolean] = "multiline" ~> colon ~> argument_boolean

  def style: Parser[Style] = "style" ~> colon ~> ident ^^ { style => Style(style) }

  def position: Parser[Position] = parseNaturalNumberTuple("position", "x", "y").map(Position.tupled)

  def size: Parser[Size] = parseNaturalNumberTuple("size", "width", "height").map(Size.tupled)

  def sizeMax: Parser[SizeMax] = parseNaturalNumberTuple("sizeMax", "width", "height").map(SizeMax.tupled)

  def sizeMin: Parser[SizeMin] = parseNaturalNumberTuple("sizeMin", "width", "height").map(SizeMin.tupled)

  private def parseNaturalNumberTuple(name: String, arg1: String, arg2: String): Parser[(Int, Int)] =
    (name ~> lp ~> arg1 ~> colon ~> natural_number <~ comma) ~ (arg2 ~> colon ~> natural_number <~ rp) ^^ { tuple =>
      val first ~ second = tuple
      (first, second)
    }
}
