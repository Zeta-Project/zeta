package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parser.EdgeParser
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Offset
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Point
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.LineParseTree
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class EdgeParserTest extends FreeSpec with Matchers with Inside {

  private def parseEdge(input: String) = {
    EdgeParser.parse(EdgeParser.edge, input)
  }

  private def createSampleEdge(name: String, conceptConnection: String, target: String = "someTarget"): String = {
    s"""
       | edge $name for $conceptConnection {
       |   target: $target
       |   placing {
       |     offset: 0.5
       |     line {
       |       point(x: 1, y: 2)
       |       point(x: 3, y: 4)
       |     }
       |   }
       | }
    """.stripMargin
  }

  "A edgeparser parser will" - {

    "succeed in parsing" - {

      "an edge" in {
        val edge = createSampleEdge("MyEdge", "MyConceptConnection", "MyTarget")
        val result = parseEdge(edge)
        result.successful shouldBe true
        result.get shouldBe EdgeParseTree(
          "MyEdge",
          "MyConceptConnection",
          Target("MyTarget"),
          List(
            Placing(
              style = None,
              Offset(0.5),
              geoModel = LineParseTree(
                style = None,
                Point(1, 2),
                Point(3, 4),
                children = Nil
              )
            )
          )
        )
      }
    }

    "fail in parsing" - {

      "an edge with invalid conceptConnection" - {

        "if name starts with a dot" in {
          val edgeWithInvalidConceptConnection = createSampleEdge("MyEdge", ".Connection")
          val result = parseEdge(edgeWithInvalidConceptConnection)
          result.successful shouldBe false
        }

        "if name ends with a dot" in {
          val edgeWithInvalidConceptConnection = createSampleEdge("MyEdge", "Connection.blub.")
          val result = parseEdge(edgeWithInvalidConceptConnection)
          result.successful shouldBe false
        }

        "if name contains two consecutive dots" in {
          val edgeWithInvalidConceptConnection = createSampleEdge("MyEdge", "Connection..blub")
          val result = parseEdge(edgeWithInvalidConceptConnection)
          result.successful shouldBe false
        }

        "if name contains multiple consecutive dots" in {
          val edgeWithInvalidConceptConnection = createSampleEdge("MyEdge", "Connection.....blub")
          val result = parseEdge(edgeWithInvalidConceptConnection)
          result.successful shouldBe false
        }

      }
    }
  }
}