package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.shape.parsetree.Attributes._
import de.htwg.zeta.parser.shape.parsetree._
import de.htwg.zeta.parser.{UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def geoModels: Parser[List[GeoModelParseTree]] = rep(geoModel)

  def geoModel: Parser[GeoModelParseTree] = ellipse | textfield | repeatingBox | line | polyline | polygon | rectangle

  private def ellipse: Parser[EllipseParseTree] = {
    val attributes = unordered(optional(style), once(position), once(size))
    "ellipse" ~> leftBrace ~> attributes ~ geoModels <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      implicit val attributeList: List[Attribute] = attributes
      EllipseParseTree(
        ?[Style],
        !![Position],
        !![Size],
        geoModels
      )
    }
  }

  private def textfield: Parser[TextfieldParseTree] = {
    val attributes = unordered(optional(style), once(identifier), optional(multiline), once(position), once(size), optional(align))
    "textfield" ~> leftBrace ~> attributes <~ rightBrace ^^ { implicit attributes =>
      TextfieldParseTree(
        ?[Style],
        !![Identifier],
        ?[Multiline].getOrElse(Multiline(false)),
        !![Position],
        !![Size],
        ?[Align].getOrElse(Align(HorizontalAlignment.middle, VerticalAlignment.middle))
      )
    }
  }

  private def repeatingBox: Parser[RepeatingBoxParseTree] = {
    val attributes = unordered(once(editable), once(foreach))
    "repeatingBox" ~> leftBrace ~> attributes ~ rep1(geoModel) <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      implicit val attributeList: List[Attribute] = attributes
      RepeatingBoxParseTree(
        !![Editable],
        !![For],
        geoModels
      )
    }
  }

  private def line: Parser[LineParseTree] = {
    val attributes = unordered(optional(style), exact(2, point))
    "line" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      implicit val attributes: List[Attribute] = parseResult
      val from :: to :: Nil = *[Point]
      LineParseTree(
        ?[Style],
        from,
        to
      )
    }
  }

  private def polyline: Parser[PolylineParseTree] = {
    val attributes = unordered(optional(style), min(2, point))
    "polyline" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      implicit val attributes: List[Attribute] = parseResult
      PolylineParseTree(
        ?[Style],
        *[Point]
      )
    }
  }

  private def polygon: Parser[PolygonParseTree] = {
    val attributes = unordered(optional(style), min(2, curvedPoint))
    "polygon" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      implicit val points: List[Attribute] = parseResult
      PolygonParseTree(
        ?[Style],
        *[CurvedPoint]
      )
    }
  }

  private def rectangle: Parser[RectangleParseTree] = {
    val attributes = unordered(optional(style), once(position), once(size), optional(curve))
    "rectangle" ~> leftBrace ~> attributes ~ geoModels <~ rightBrace ^^ {parseResult =>
      val attributes ~ geoModels = parseResult
      implicit val attributeList: List[Attribute] = attributes
      RectangleParseTree(
        ?[Style],
        !![Position],
        !![Size],
        ?[Curve],
        geoModels
      )
    }
  }

  private def style = include(AttributeParser.style)

  private def position = include(AttributeParser.position)

  private def point = include(AttributeParser.point)

  private def curvedPoint = include(AttributeParser.curvedPoint)

  private def size = include(AttributeParser.size)

  private def align = include(AttributeParser.align)

  private def identifier = include(AttributeParser.identifier)

  private def multiline = include(AttributeParser.multiline)

  private def editable = include(AttributeParser.editable)

  private def foreach = include(AttributeParser.foreach)

  private def curve = include(AttributeParser.curve)
}
