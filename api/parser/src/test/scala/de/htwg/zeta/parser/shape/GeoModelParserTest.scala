package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parser.GeoModelParser
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees._
import de.htwg.zeta.parser.shape.parsetree._
import org.scalatest.{FreeSpec, Inside, Matchers}

class GeoModelParserTest extends FreeSpec with Matchers with Inside {

  private def parseGeoModel(input: String) = {
    GeoModelParser.parse(GeoModelParser.geoModel, input)
  }

  "A geomodel parser will" - {

    "succeed in parsing" - {

      "a roundedRectangle" in {
        val roundedRectangle =
          """
            |roundedRectangle {
            |  curve(width: 10, height: 1)
            |  position(x: 0, y: 0)
            |  size(width: 100, height: 20)
            |}
          """.stripMargin
        val result = parseGeoModel(roundedRectangle)
        result.successful shouldBe true
        result.get shouldBe RoundedRectangleParseTree(
          style = None,
          Position(0, 0),
          Size(100, 20),
          Curve(10, 1),
          children = Nil
        )
      }

      "a statictext" in {
        val statictext =
          """
            |statictext {
            |  text: "Hey Zeta, whats up?"
            |  position(x: 0, y: 0)
            |  size(width: 100, height: 20)
            |}
          """.stripMargin
        val result = parseGeoModel(statictext)
        result.successful shouldBe true
        result.get shouldBe StatictextParseTree(
          style = None,
          Size(100, 20),
          Position(0, 0),
          text = Text("Hey Zeta, whats up?"),
          children = Nil
        )
      }

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
          Point(3, 4),
          children = Nil
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
          ),
          children = Nil
        )
      }

      "a polygon" in {
        val polygon =
          """
            |polygon {
            |  style: PolygonStyle
            |  point(x: 1, y: 2)
            |  point(x: 3, y: 4)
            |  point(x: 5, y: 6)
            |}
          """.stripMargin
        val result = parseGeoModel(polygon)
        result.successful shouldBe true
        result.get shouldBe PolygonParseTree(
          Some(Style("PolygonStyle")),
          List(
            Point(1, 2),
            Point(3, 4),
            Point(5, 6)
          ),
          children = Nil
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
          For(each = Identifier("hatKind"), as = "kind"),
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
            |  position(x: 3, y: 4)
            |  size(width:10, height:15)
            |
            |  line {
            |    point(x: 1, y: 4)
            |    point(x: 3, y: 5)
            |  }
            |}
          """.stripMargin
        val result = parseGeoModel(rectangle)
        result.successful shouldBe true
        result.get shouldBe RectangleParseTree(
          Some(Style("RectStyle")),
          Position(3, 4),
          Size(10, 15),
          List(
            LineParseTree(
              style = None,
              Point(1, 4),
              Point(3, 5),
              children = Nil
            )
          )
        )
      }

      "a horizontalLayout" in {
        val horizontalLayout =
          """
            |horizontalLayout {
            |  rectangle {
            |    position(x: 5, y: 10)
            |    size(width: 10, height: 20)
            |  }
            |  rectangle {
            |    position(x: 15, y: 20)
            |    size(width: 20, height: 40)
            |  }
            |}
          """.stripMargin
        val result = parseGeoModel(horizontalLayout)
        result.successful shouldBe true
        result.get shouldBe HorizontalLayoutParseTree(
          style = None,
          List(
            RectangleParseTree(
              style = None,
              Position(5, 10),
              Size(10, 20),
              children = Nil
            ),
            RectangleParseTree(
              style = None,
              Position(15, 20),
              Size(20, 40),
              children = Nil
            )
          )
        )
      }

      "a verticalLayout" in {
        val verticalLayout =
          """
            |verticalLayout {
            |  rectangle {
            |    position(x: 5, y: 10)
            |    size(width: 10, height: 20)
            |  }
            |  rectangle {
            |    position(x: 15, y: 20)
            |    size(width: 20, height: 40)
            |  }
            |}
          """.stripMargin
        val result = parseGeoModel(verticalLayout)
        result.successful shouldBe true
        result.get shouldBe VerticalLayoutParseTree(
          style = None,
          List(
            RectangleParseTree(
              style = None,
              Position(5, 10),
              Size(10, 20),
              children = Nil
            ),
            RectangleParseTree(
              style = None,
              Position(15, 20),
              Size(20, 40),
              children = Nil
            )
          )
        )
      }
    }
  }

}
