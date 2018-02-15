package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parser.GeoModelParser
import de.htwg.zeta.parser.shape.parsetree.Attributes._
import de.htwg.zeta.parser.shape.parsetree._
import org.scalatest.{FreeSpec, Inside, Matchers}

class GeoModelParserTest extends FreeSpec with Matchers with Inside {

  private def parseGeoModel(input: String) = {
    GeoModelParser.parse(GeoModelParser.geoModel, input)
  }

  "A geomodel parser will" - {

    "succeed in parsing" - {

      "a line" in {
        val line =
          """
            |line {
            |  style: BoldLineStyle
            |  point(x: 1, y: 2)
            |  point(x: 3, y: 4)
            |}
          """.stripMargin
        val result = parseGeoModel(line)
        result.successful shouldBe true
        result.get shouldBe LineParseTree(
          Some(Style("BoldLineStyle")),
          Point(1, 2),
          Point(3, 4)
        )
      }

      "a polyline" in {
        val polyline =
          """
            |polyline {
            |  style: BlaBla
            |  point(x: 1, y: 2)
            |  point(x: 3, y: 4)
            |  point(x: 5, y: 6)
            |  point(x: 7, y: 8)
            |  point(x: 9, y: 0)
            |}
          """.stripMargin
        val result = parseGeoModel(polyline)
        result.successful shouldBe true
        result.get shouldBe PolylineParseTree(
          Some(Style("BlaBla")),
          List(
            Point(1, 2),
            Point(3, 4),
            Point(5, 6),
            Point(7, 8),
            Point(9, 0),
          )
        )
      }

      "a polygon" in {
        val polygon =
          """
            |polygon {
            |  style: PolygonStyle
            |  point(x: 1, y: 2, curveBefore: 1, curveAfter: 1)
            |  point(x: 3, y: 4, curveBefore: 1, curveAfter: 1)
            |  point(x: 5, y: 6, curveBefore: 1, curveAfter: 1)
            |}
          """.stripMargin
        val result = parseGeoModel(polygon)
        result.successful shouldBe true
        result.get shouldBe PolygonParseTree(
          Some(Style("PolygonStyle")),
          List(
            CurvedPoint(1, 2, 1, 1),
            CurvedPoint(3, 4, 1, 1),
            CurvedPoint(5, 6, 1, 1)
          )
        )
      }

      "a repeating box" in {
        val repeatingBox =
          """
            |repeatingBox {
            |  editable: true
            |  for(each: hatKind, as: kind)
            |
            |  ellipse {
            |    style: None
            |    position(x: 1, y: 1)
            |    size(width: 100, height: 20)
            |  }
            |}
          """.stripMargin
        val result = parseGeoModel(repeatingBox)
        result.successful shouldBe true
        result.get shouldBe RepeatingBoxParseTree(
          Editable(true),
          For(each = "hatKind", as = "kind"),
          List(
            EllipseParseTree(
              Some(Style("None")),
              Position(1, 1),
              Size(100, 20),
              Nil
            )
          )
        )
      }

      "a rectangle" in {
        val rectangle =
          """
            |rectangle {
            |  style: RectStyle
            |  position(x:3, y:4)
            |  size(width:10, height:15)
            |  curve(width:3, height:10)
            |
            |  line {
            |    point(x:1, y:4)
            |    point(x:3, y:5)
            |  }
            |}
          """.stripMargin
        val result = parseGeoModel(rectangle)
        result.successful shouldBe true
        result.get shouldBe RectangleParseTree(
          Some(Style("RectStyle")),
          Position(3,4),
          Size(10, 15),
          Some(Curve(3, 10)),
          List(
            LineParseTree(
              style = None,
              Point(1, 4),
              Point(3, 5)
            )
          )
        )
      }
    }
  }

}
