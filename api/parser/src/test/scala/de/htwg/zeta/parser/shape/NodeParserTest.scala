package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parser.NodeParser
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Align
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.HorizontalAlignment
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Multiline
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Point
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.VerticalAlignment
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.EllipseParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HorizontalLayoutParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.LineParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TextfieldParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.AbsoluteAnchor
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.AnchorPosition
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.NodeStyle
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.PredefinedAnchor
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.RelativeAnchor
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.Resizing
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMax
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMin
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import org.scalatest.Inside
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class NodeParserTest extends AnyFreeSpec with Matchers with Inside {

  private def parse(input: String) = {
    NodeParser.parse(NodeParser.node, input)
  }

  "A node parser will" - {

    "succeed in parsing" - {

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
        val result = parse(nodeWithEdges)
        result.successful shouldBe true
        val node = result.get
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
        val result = parse(nodeWithAnchors)
        result.successful shouldBe true
        val node = result.get
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
        val result = parse(nodeWithAttributes)
        result.successful shouldBe true
        val node = result.get
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
        val result = parse(nodeWithUnorderedAttributes)
        result.successful shouldBe true
        val node = result.get
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
            |    horizontalLayout {
            |
            |      textfield {
            |        identifier: ueberschrift
            |        multiline: false
            |        position(x: 3, y: 4)
            |        size(width: 10, height: 15)
            |        align(horizontal: middle, vertical: middle)
            |      }
            |
            |      line {
            |        point(x: 1, y: 1)
            |        point(x: 5, y: 10)
            |      }
            |    }
            |  }
            |
            |}
          """.stripMargin
        val result = parse(fullNodeExample)
        result.successful shouldBe true
        val node = result.get
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
              Some(Style("BlackWhiteStyle")),
              Position(3, 4),
              Size(10, 15),
              List(
                HorizontalLayoutParseTree(
                  style = None,
                  List(
                    TextfieldParseTree(
                      style = None,
                      Identifier("ueberschrift"),
                      textBody = None,
                      Position(3, 4),
                      Size(10, 15),
                      multiline = Some(Multiline(false)),
                      Some(Align(
                        HorizontalAlignment.middle,
                        VerticalAlignment.middle
                      )),
                      editable = None,
                      children = Nil
                    ),
                    LineParseTree(
                      style = None,
                      Point(1, 1),
                      Point(5, 10),
                      children = Nil
                    )
                  )
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
        val result = parse(nodeWithNegativeSize)
        result.successful shouldBe false
      }

      "a node without attributes" in {
        val nodeWithoutAttributes =
          """
            |node MyNode for SomeConceptClass {
            |}
          """.stripMargin
        val result = parse(nodeWithoutAttributes)
        result.successful shouldBe false
      }

      "an invalid input" in {
        val notANode = "bla bla"
        val result = parse(notANode)
        result.successful shouldBe false
      }
    }
  }
}
