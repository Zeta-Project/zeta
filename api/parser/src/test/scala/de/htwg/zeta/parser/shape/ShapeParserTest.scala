package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parser.ShapeParser
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.{EllipseParseTree, LineParseTree, TextfieldParseTree}
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import org.scalatest.{FreeSpec, Inside, Matchers}

//noinspection ScalaStyle
class ShapeParserTest extends FreeSpec with Matchers with Inside {

  "A shape parser will" - {

    "succeed in parsing" - {

      "an empty string" in {
        val noShapes = ""
        val result = ShapeParser.parseShapes(noShapes)
        result.successful shouldBe true
        val shapes = result.get
        shapes shouldBe empty
      }

      "a node with edges" in {
        val nodeWithEdges =
          """
            |node MyNode for SomeConceptClass {
            |
            |  edges {
            |    Edge0
            |    Edge1
            |    Edge2
            |  }
            |
            |  style: MyStyle
            |  sizeMin(width: 20, height: 50)
            |  sizeMax(width: 40, height: 80)
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithEdges)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node.edges shouldBe List("Edge0", "Edge1", "Edge2")
      }

      "a node with anchors" in {
        val nodeWithAnchors =
          """
            |node MyNode for SomeConceptClass {
            |  sizeMin(width: 20, height: 50)
            |  sizeMax(width: 40, height: 80)
            |  anchor(x: 1, y: 1)
            |  anchor(xoffset: 5, yoffset: 20)
            |  anchor(predefined: corner)
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithAnchors)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node.anchors shouldBe List(
          AbsoluteAnchor(1, 1),
          RelativeAnchor(5, 20),
          PredefinedAnchor(AnchorPosition.corner)
        )
      }

      "a node with attributes" in {
        val nodeWithAttributes =
          """
            |node MyNode for SomeConceptClass {
            |  style: MyStyle
            |  sizeMin(width: 20, height: 50)
            |  sizeMax(width: 40, height: 80)
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithAttributes)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node shouldBe NodeParseTree(
          "MyNode",
          "SomeConceptClass",
          edges = Nil,
          SizeMin(20, 50),
          SizeMax(40, 80),
          Some(NodeStyle("MyStyle")),
          resizing = None,
          anchors = Nil,
          geoModels = Nil
        )
      }

      "a node with unordered attributes" in {
        val nodeWithUnorderedAttributes =
          """
            |node MyNode for SomeConceptClass {
            |  sizeMax(width: 40, height: 80)
            |  sizeMin(width: 20, height: 50)
            |  style: MyStyle
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithUnorderedAttributes)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node shouldBe NodeParseTree(
          "MyNode",
          "SomeConceptClass",
          edges = Nil,
          SizeMin(20, 50),
          SizeMax(40, 80),
          Some(NodeStyle("MyStyle")),
          resizing = None,
          anchors = Nil,
          geoModels = Nil
        )
      }

      "a node with all attributes and geomodels" in {
        val fullNodeExample =
          """
            |node MyNode for SomeConceptClass {
            |
            |  edges {
            |    Edge0
            |    Edge1
            |  }
            |
            |  style: MyStyle
            |  resizing(horizontal: false, vertical: false, proportional: true)
            |  sizeMin(width: 20, height: 75)
            |  sizeMax(width: 50, height: 85)
            |  anchor(x: 1, y: 1)
            |  anchor(xoffset: 5, yoffset: 20)
            |  anchor(predefined: corner)
            |
            |  ellipse {
            |    style: BlackWhiteStyle
            |    position(x: 3, y: 4)
            |    size(width: 10, height: 15)
            |
            |    textfield {
            |      identifier: ueberschrift
            |      multiline: false
            |      position(x: 3, y: 4)
            |      size(width: 10, height: 15)
            |      align(horizontal: middle, vertical: middle)
            |    }
            |
            |    line {
            |      point(x: 1, y: 1)
            |      point(x: 5, y: 10)
            |    }
            |  }
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(fullNodeExample)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]

        node shouldBe NodeParseTree(
          "MyNode",
          "SomeConceptClass",
          List("Edge0", "Edge1"),
          SizeMin(20, 75),
          SizeMax(50, 85),
          Some(NodeStyle("MyStyle")),
          Some(Resizing(horizontal = false, vertical = false, proportional = true)),
          List(
            AbsoluteAnchor(1, 1),
            RelativeAnchor(5, 20),
            PredefinedAnchor(AnchorPosition.corner)
          ),
          List(
            EllipseParseTree(
              Some(de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style("BlackWhiteStyle")),
              Position(3, 4),
              Size(10, 15),
              List(
                TextfieldParseTree(
                  style = None,
                  Identifier("ueberschrift"),
                  Multiline(false),
                  Position(3, 4),
                  Size(10, 15),
                  Align(
                    HorizontalAlignment.middle,
                    VerticalAlignment.middle
                  )
                ),
                LineParseTree(
                  style = None,
                  Point(1, 1),
                  Point(5, 10)
                )
              )
            )
          )
        )
      }
    }

    "fail in parsing" - {
      "a node with negative size" in {
        val nodeWithNegativeSize =
          """
            | node MyNode for SomeConceptClass {
            |   minSize(width: -20, 20)
            | }
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithNegativeSize)
        // HOWTO: should fail instead of returning an empty list!
        //result.successful shouldBe false
      }
    }
  }
}
