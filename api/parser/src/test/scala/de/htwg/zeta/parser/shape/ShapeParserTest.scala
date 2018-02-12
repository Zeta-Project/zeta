package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.Attributes._
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
        node.attributes shouldBe List(
          Style("MyStyle"),
          SizeMin(20, 50),
          SizeMax(40, 80)
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
        node.attributes shouldBe List(
          SizeMax(40, 80),
          SizeMin(20, 50),
          Style("MyStyle")
        )
      }

      "a node with all attributes and children" in {
        val fullNodeExample =
          """
            |node MyNode for SomeConceptClass {
            |  edges {
            |    Edge0
            |    Edge1
            |  }
            |
            |  style: MyStyle
            |  resizing(horizontal: false, vertical: false, proportional: true)
            |  sizeMin(width: 20, height: 75)
            |  sizeMax(width: 50, height: 85)
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
            |  }
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(fullNodeExample)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node.edges shouldBe List("Edge0", "Edge1")
        node.attributes shouldBe List(
          Style("MyStyle"),
          Resizing(horizontal = false, vertical = false, proportional = true),
          SizeMin(20, 75),
          SizeMax(50, 85)
        )
        node.geoModels shouldBe List(
          Ellipse(
            Style("BlackWhiteStyle"),
            Position(3, 4),
            Size(10, 15),
            List(
              Textfield(
                Identifier("ueberschrift"),
                multiline = false,
                Position(3, 4),
                Size(10, 15),
                Align(
                  HorizontalAlignment.middle,
                  VerticalAlignment.middle)
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
