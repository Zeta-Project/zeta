package de.htwg.zeta.parser

import de.htwg.zeta.server.generator.parser.CommonParserMethods
import org.scalatest.FreeSpec
import org.scalatest.Matchers

/**
 */
class UniteParsersTest extends FreeSpec with Matchers {

  "Multiple Parsers extending 'UniteParsers'" - {

    object NodeParser extends CommonParserMethods with UniteParsers{
      def parseNode = "node" ~ "{}"
    }
    object EdgeParser extends CommonParserMethods with UniteParsers{
      def parseEdge = "edge" ~ "{}"
    }

    object DiagramParser extends CommonParserMethods with UniteParsers {

      def parseNodeExplicit = unite(NodeParser.parseNode)
      def parseEdgeExplicit = unite(EdgeParser.parseEdge)

      def parseNodeImplicit: Parser[NodeParser.~[String, String]] = NodeParser.parseNode
      def parseEdgeImplicit: EdgeParser.Parser[EdgeParser.~[String, String]] = EdgeParser.parseEdge


      def parseDiagramExplicit = "diagram" ~ "{"~ rep(parseNodeExplicit | parseEdgeExplicit) ~"}"

      def parseDiagramImplicit = "diagram" ~ "{"~ rep(parseNodeImplicit | parseEdgeImplicit) ~"}"

      def parseDiagramIdentify = "diagram" ~ "{"~ rep(NodeParser.parseNode.$identify | EdgeParser.parseEdge.$identify) ~"}"
    }

    "will parse a simple example and unite the parsers" -{
      val dia =
        """|
           |diagram {
           |  node {}
           |  edge {}
           |  node {}
           |}""".stripMargin

      "explicitely by calling the unite method" in {
        DiagramParser.parse(DiagramParser.parseDiagramExplicit, dia).successful shouldBe true
      }

      "implicitly within typed method calls" in {
        DiagramParser.parse(DiagramParser.parseDiagramImplicit, dia).successful shouldBe true
      }

      "implicitly by calling an $identify method" in {
        DiagramParser.parse(DiagramParser.parseDiagramIdentify, dia).successful shouldBe true
      }

    }
  }

}
