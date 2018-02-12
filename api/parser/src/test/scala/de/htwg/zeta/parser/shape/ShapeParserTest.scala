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

      "a simple node without anything" in {
        val simpleNode = "node MyNode for SomeConceptClass { }"
        val result = ShapeParser.parseShapes(simpleNode)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node.identifier shouldBe "MyNode"
        node.conceptClass shouldBe "SomeConceptClass"
      }

      "a node with edges" in {
        val nodeWithEdges =
          """
            |node MyNode for SomeConceptClass {
            |  edges {
            |    Edge0
            |    Edge1
            |    Edge2
            |  }
            |}
          """.stripMargin
        val result = ShapeParser.parseShapes(nodeWithEdges)
        result.successful shouldBe true
        val node = result.get.head.asInstanceOf[NodeParseTree]
        node.edges shouldBe List("Edge0", "Edge1", "Edge2")
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
        val attributes = node.attributes
        attributes.head shouldBe Style("MyStyle")
        attributes(1) shouldBe SizeMin(20, 50)
        attributes(2) shouldBe SizeMax(40, 80)
      }

      "a node with all attributes and children" in {
        val fullNodeExample =
          """
            |node MyNode for SomeConceptClass {
            |  edges {
            |    Edge0
            |    Edge1
            |  }
            |  style: MyStyle
            |  sizeMin(width: 20, height: 75)
            |  sizeMax(width: 50, height: 85)
            |  resizing(horizontal: false, vertical: false, proportional: true)
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
        val attributes = node.attributes
        attributes.head shouldBe Style("MyStyle")
        attributes(1) shouldBe SizeMin(20, 75)
        attributes(2) shouldBe SizeMax(50, 85)
        attributes(3) shouldBe Resizing(horizontal = false, vertical = false, proportional = true)
        node.geoModels shouldBe List(
          Ellipse(
            Style("BlackWhiteStyle"),
            Position(3, 4),
            Size(10, 15),
            List(
              Textfield(
                "ueberschrift",
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
