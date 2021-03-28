package de.htwg.zeta.parser.shape.parser

import de.htwg.zeta.parser.Collector
import de.htwg.zeta.parser.CommonParserMethods
import de.htwg.zeta.parser.UniteParsers
import de.htwg.zeta.parser.UnorderedParser
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Align
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Curve
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Editable
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.For
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.GeoModelAttribute
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Multiline
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Point
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Text
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.TextBody
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.{DiamondParseTree, EllipseParseTree, GeoModelParseTree, HexagonParseTree, HorizontalLayoutParseTree, LineParseTree, OctagonParseTree, PolygonParseTree, PolylineParseTree, RectangleParseTree, RepeatingBoxParseTree, RoundedRectangleParseTree, Star8ParseTree, StatictextParseTree, TextfieldParseTree, TriangleParseTree, VerticalLayoutParseTree}

object GeoModelParser extends CommonParserMethods with UniteParsers with UnorderedParser {

  def geoModels: Parser[List[GeoModelParseTree]] = rep(geoModel)

  def geoModel: Parser[GeoModelParseTree] = ellipse | textfield | repeatingBox | line | polyline | polygon |
    rectangle | horizontalLayout | verticalLayout | statictext | roundedRectangle | triangle | hexagon | octagon |
    diamond | star8

  private def parseGeoModel(name: String, attributes: List[ParseConf[GeoModelAttribute]]): Parser[(Collector, List[GeoModelParseTree])] = {
    (name ~! leftBrace ~ unordered(attributes: _*) ~ geoModels ~ rightBrace).map {
      case _ ~ _ ~ attrs ~ geoModels ~ _ => (Collector(attrs), geoModels)
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

  private def triangle: Parser[TriangleParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("triangle", attributes).map {
      case (attrs, geoModels) => TriangleParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def hexagon: Parser[HexagonParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("hexagon", attributes).map {
      case (attrs, geoModels) => HexagonParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def octagon: Parser[OctagonParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("octagon", attributes).map {
      case (attrs, geoModels) => OctagonParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def diamond: Parser[DiamondParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("diamond", attributes).map {
      case (attrs, geoModels) => DiamondParseTree(
        attrs.?[Style],
        attrs.![Position],
        attrs.![Size],
        geoModels
      )
    }
  }

  private def star8: Parser[Star8ParseTree] = {
    val attributes = List(optional(style), once(position), once(size))
    parseGeoModel("star8", attributes).map {
      case (attrs, geoModels) => Star8ParseTree(
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
    val attributes = List(
      optional(style), once(identifier), optional(textBody), optional(multiline), once(position), once(size), optional(align), optional(editable)
    )
    parseGeoModel("textfield", attributes).map {
      case (attrs, geoModels) => TextfieldParseTree(
        attrs.?[Style],
        attrs.![Identifier],
        attrs.?[TextBody],
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
    val attributes = List(optional(style), once(position), once(size), once(curve))
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

  private def text = include(GeoModelAttributeParser.text).named("text")

  private def textBody = include(GeoModelAttributeParser.textBody).named("textBody")

  private def style = include(GeoModelAttributeParser.style).named("style")

  private def position = include(GeoModelAttributeParser.position).named("position")

  private def point = include(GeoModelAttributeParser.point).named("point")

  private def size = include(GeoModelAttributeParser.size).named("size")

  private def align = include(GeoModelAttributeParser.align).named("align")

  private def identifier = include(GeoModelAttributeParser.identifier).named("identifier")

  private def multiline = include(GeoModelAttributeParser.multiline).named("multiline")

  private def editable = include(GeoModelAttributeParser.editable).named("editable")

  private def foreach = include(GeoModelAttributeParser.foreach).named("foreach")

  private def curve = include(GeoModelAttributeParser.curve).named("curve")
}
