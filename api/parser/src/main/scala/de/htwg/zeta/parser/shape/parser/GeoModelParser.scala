package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees._
import de.htwg.zeta.parser.{Collector, UniteParsers, UnorderedParser}
import de.htwg.zeta.server.generator.parser.CommonParserMethods

object GeoModelParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def geoModels: Parser[List[GeoModelParseTree]] = rep(geoModel)

  def geoModel: Parser[GeoModelParseTree] = ellipse | textfield | repeatingBox | line | polyline | polygon |
    rectangle | horizontalLayout | verticalLayout | statictext | roundedRectangle

  private def parseGeoModel(name: String, attributes: List[ParseConf[GeoModelAttribute]]): Parser[(Collector, List[GeoModelParseTree])] = {
    (name ~> leftBrace ~> unordered(attributes: _*) ~ geoModels <~ rightBrace).map {
      case attrs ~ geoModels => (Collector(attrs), geoModels)
    }
  }

  private def ellipse: Parser[EllipseParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("ellipse", attributes).map {
      case (attrs, geoModels) => EllipseParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def statictext: Parser[StatictextParseTree] = {
    val attributes = List(optional(style), once(position), once(size), once(text))
    parseGeoModel("statictext", attributes).map {
      case (attrs, geoModels) => StatictextParseTree(
        attrs.?[Style],
        attrs.![Size],
        attrs.![Position],
        attrs.![Text],
        geoModels
      )
    }
  }

  private def textfield: Parser[TextfieldParseTree] = {
    val attributes = List(optional(style), once(identifier), optional(multiline), once(position), once(size), optional(align))
    parseGeoModel("textfield", attributes).map {
      case (attrs, geoModels) => TextfieldParseTree(
        attrs.?[Style],
        attrs.![Identifier],
        attrs.![Position],
        attrs.![Size],
        attrs.?[Multiline],
        attrs.?[Align],
        attrs.?[Editable],
        geoModels
      )
    }
  }

  private def repeatingBox: Parser[RepeatingBoxParseTree] = {
    val attributes = List(once(editable), once(foreach))
    parseGeoModel("repeatingBox", attributes).map {
      case (attrs, geoModels) => RepeatingBoxParseTree(
        attrs.![Editable],
        attrs.![For],
        geoModels
      )
    }
  }

  private def line: Parser[LineParseTree] = {
    val attributes = List(optional(style), exact(2, point))
    parseGeoModel("line", attributes).map {
      case (attrs, geoModels) =>
        val startPoint :: endPoint :: Nil = attrs.*[Point]
        LineParseTree(
          attrs.?[Style],
          startPoint,
          endPoint,
          geoModels
        )
    }
  }

  private def polyline: Parser[PolylineParseTree] = {
    val attributes = List(optional(style), min(2, point))
    parseGeoModel("polyline", attributes).map {
      case (attrs, geoModels) =>
        PolylineParseTree(
          attrs.?[Style],
          attrs.*[Point],
          geoModels
        )
    }
  }

  private def polygon: Parser[PolygonParseTree] = {
    val attributes = List(optional(style), min(2, point))
    parseGeoModel("polygon", attributes).map {
      case (attrs, geoModels) =>
        PolygonParseTree(
          attrs.?[Style],
          attrs.*[Point],
          geoModels
        )
    }
  }

  private def rectangle: Parser[RectangleParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("rectangle", attributes).map {
      case (attrs, geoModels) =>
        RectangleParseTree(
          attrs.?[Style],
          attrs.![Position],
          attrs.![Size],
          geoModels
        )
    }
  }

  private def roundedRectangle: Parser[RoundedRectangleParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("roundedRectangle", attributes).map {
      case (attrs, geoModels) =>
        RoundedRectangleParseTree(
          attrs.?[Style],
          attrs.![Position],
          attrs.![Size],
          attrs.![Curve],
          geoModels
        )
    }
  }

  private def horizontalLayout: Parser[HorizontalLayoutParseTree] = {
    val attributes = List(optional(style))
    parseGeoModel("horizontalLayout", attributes).map {
      case (attrs, geoModels) =>
        HorizontalLayoutParseTree(
          attrs.?[Style],
          geoModels
        )
    }
  }

  private def verticalLayout: Parser[VerticalLayoutParseTree] = {
    val attributes = List(optional(style))
    parseGeoModel("verticalLayout", attributes).map {
      case (attrs, geoModels) =>
        VerticalLayoutParseTree(
          attrs.?[Style],
          geoModels
        )
    }
  }

  private def text = include(GeoModelAttributeParser.text)

  private def style = include(GeoModelAttributeParser.style)

  private def position = include(GeoModelAttributeParser.position)

  private def point = include(GeoModelAttributeParser.point)

  private def size = include(GeoModelAttributeParser.size)

  private def align = include(GeoModelAttributeParser.align)

  private def identifier = include(GeoModelAttributeParser.identifier)

  private def multiline = include(GeoModelAttributeParser.multiline)

  private def editable = include(GeoModelAttributeParser.editable)

  private def foreach = include(GeoModelAttributeParser.foreach)

  private def curve = include(GeoModelAttributeParser.curve)
}
