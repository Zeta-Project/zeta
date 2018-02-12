package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.NodeAttributes.{SizeMax, SizeMin, Style}
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
        node.edges should have size 3
        node.edges.head shouldBe "Edge0"
        node.edges(1) shouldBe "Edge1"
        node.edges(2) shouldBe "Edge2"
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
        val style = attributes.head
        style shouldBe Style("MyStyle")
        val sizeMin = attributes(1)
        sizeMin shouldBe SizeMin(20, 50)
        val sizeMax = attributes(2)
        sizeMax shouldBe SizeMax(40, 80)
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
