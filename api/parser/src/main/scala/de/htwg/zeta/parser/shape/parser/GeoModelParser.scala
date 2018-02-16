package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees._
import de.htwg.zeta.parser.{Collector, UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def geoModels: Parser[List[GeoModelParseTree]] = rep(geoModel)

  def geoModel: Parser[GeoModelParseTree] = ellipse | textfield | repeatingBox | line | polyline | polygon | rectangle

  private def ellipse: Parser[EllipseParseTree] = {
    val attributes = unordered(optional(style), once(position), once(size))
    "ellipse" ~> leftBrace ~> attributes ~ geoModels <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      val attrs = Collector(attributes)
      EllipseParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def textfield: Parser[TextfieldParseTree] = {
    val attributes = unordered(optional(style), once(identifier), optional(multiline), once(position), once(size), optional(align))
    "textfield" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      TextfieldParseTree(
        attrs.?[Style],
        attrs.![Identifier],
        attrs.?[Multiline].getOrElse(Multiline(false)),
        attrs.![Position],
        attrs.![Size],
        attrs.?[Align].getOrElse(Align(HorizontalAlignment.middle, VerticalAlignment.middle))
      )
    }
  }

  private def repeatingBox: Parser[RepeatingBoxParseTree] = {
    val attributes = unordered(once(editable), once(foreach))
    "repeatingBox" ~> leftBrace ~> attributes ~ rep1(geoModel) <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      val attrs = Collector(attributes)
      RepeatingBoxParseTree(
        attrs.![Editable],
        attrs.![For],
        geoModels
      )
    }
  }

  private def line: Parser[LineParseTree] = {
    val attributes = unordered(optional(style), exact(2, point))
    "line" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      val from :: to :: Nil = attrs.*[Point]
      LineParseTree(
        attrs.?[Style],
        from,
        to
      )
    }
  }

  private def polyline: Parser[PolylineParseTree] = {
    val attributes = unordered(optional(style), min(2, point))
    "polyline" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      PolylineParseTree(
        attrs.?[Style],
        attrs.*[Point]
      )
    }
  }

  private def polygon: Parser[PolygonParseTree] = {
    val attributes = unordered(optional(style), min(2, curvedPoint))
    "polygon" ~> leftBrace ~> attributes <~ rightBrace ^^ { parseResult =>
      val attrs = Collector(parseResult)
      PolygonParseTree(
        attrs.?[Style],
        attrs.*[CurvedPoint]
      )
    }
  }

  private def rectangle: Parser[RectangleParseTree] = {
    val attributes = unordered(optional(style), once(position), once(size), optional(curve))
    "rectangle" ~> leftBrace ~> attributes ~ geoModels <~ rightBrace ^^ { parseResult =>
      val attributes ~ geoModels = parseResult
      val attrs = Collector(attributes)
      RectangleParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        attrs.?[Curve],
        geoModels
      )
    }
  }

  private def style = include(GeoModelAttributeParser.style)

  private def position = include(GeoModelAttributeParser.position)

  private def point = include(GeoModelAttributeParser.point)

  private def curvedPoint = include(GeoModelAttributeParser.curvedPoint)

  private def size = include(GeoModelAttributeParser.size)

  private def align = include(GeoModelAttributeParser.align)

  private def identifier = include(GeoModelAttributeParser.identifier)

  private def multiline = include(GeoModelAttributeParser.multiline)

  private def editable = include(GeoModelAttributeParser.editable)

  private def foreach = include(GeoModelAttributeParser.foreach)

  private def curve = include(GeoModelAttributeParser.curve)
}
