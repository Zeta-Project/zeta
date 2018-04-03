package de.htwg.zeta.parser.diagram

import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class DiagramParserTest extends FreeSpec with Matchers with Inside {

  "A diagram parser will" - {

    "succeed in parsing" - {

      "an empty string" in {
        val noDiagrams = ""
        val result = DiagramParser.parseDiagrams(noDiagrams)
        result.successful shouldBe true
        val diagrams = result.get
        diagrams shouldBe empty
      }

      "a diagram without palettes" in {
        val diagramWithoutPalettes = "diagram myDiagram { }"
        val result = DiagramParser.parseDiagrams(diagramWithoutPalettes)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes shouldBe empty
      }

      "a diagram with a single empty palette" in {
        val diagramWithSingleEmptyPalette =
          """
            |diagram myDiagram {
            |  palette myPalette { }
            |}
          """.stripMargin
        val result = DiagramParser.parseDiagrams(diagramWithSingleEmptyPalette)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes should have size 1
        val palette = diagram.palettes.head
        palette.name shouldBe "myPalette"
        palette.nodes shouldBe empty
      }

      "a diagram with a single node" in {
        val diagramWithSingleNode =
          """
            |diagram myDiagram {
            |  palette myPalette {
            |    myNode
            |  }
            |}
          """.stripMargin
        val result = DiagramParser.parseDiagrams(diagramWithSingleNode)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes should have size 1
        val palette = diagram.palettes.head
        palette.name shouldBe "myPalette"
        palette.nodes should have size 1
        val node = palette.nodes.head
        node.name shouldBe "myNode"
      }

      "a diagram with multiple nodes" in {
        val diagramWithMultipleNodes =
          """
            |diagram myDiagram {
            |  palette myPalette {
            |    node0
            |    node1
            |    node2
            |  }
            |}
          """.stripMargin
        val result = DiagramParser.parseDiagrams(diagramWithMultipleNodes)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes.size shouldBe 1
        val palette = diagram.palettes.head
        palette.name shouldBe "myPalette"
        palette.nodes should have size 3
        palette.nodes.head.name shouldBe "node0"
        palette.nodes(1).name shouldBe "node1"
        palette.nodes(2).name shouldBe "node2"
      }

      "a diagram with multiple empty palettes" in {
        val diagramWithMultipleEmptyPalettes =
          """
            |diagram myDiagram {
            |  palette p0 { }
            |  palette p1 { }
            |  palette p2 { }
            |}
          """.stripMargin
        val result = DiagramParser.parseDiagrams(diagramWithMultipleEmptyPalettes)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes should have size 3
        val palette0 = diagram.palettes.head
        palette0.name shouldBe "p0"
        palette0.nodes shouldBe empty
        val palette1 = diagram.palettes(1)
        palette1.name shouldBe "p1"
        palette1.nodes shouldBe empty
        val palette2 = diagram.palettes(2)
        palette2.name shouldBe "p2"
        palette2.nodes shouldBe empty
      }

      "multiple diagrams" in {
        val diagrams =
          """
            |diagram BaumDiagramm {
            |  palette BaumElemente {
            |    KnotenNode
            |  }
            |}
            |diagram MatroschkaDiagramm {
            |  palette MatroschkaElemente {
            |    MatroschkaNode
            |  }
            |}
          """.stripMargin
        val result = DiagramParser.parseDiagrams(diagrams)
        result.successful shouldBe true
        val tree = result.get
        tree should have size 2

        val baumDiagramm = tree.head
        baumDiagramm.name shouldBe "BaumDiagramm"
        baumDiagramm.palettes should have size 1
        baumDiagramm.palettes.head.name shouldBe "BaumElemente"
        baumDiagramm.palettes.head.nodes should have size 1
        baumDiagramm.palettes.head.nodes.head.name shouldBe "KnotenNode"

        val matroschkaDiagramm = tree(1)
        matroschkaDiagramm.name shouldBe "MatroschkaDiagramm"
        matroschkaDiagramm.palettes should have size 1
        matroschkaDiagramm.palettes.head.name shouldBe "MatroschkaElemente"
        matroschkaDiagramm.palettes.head.nodes should have size 1
        matroschkaDiagramm.palettes.head.nodes.head.name shouldBe "MatroschkaNode"
      }

    }

    "fail in parsing" - {

      "an invalid diagram" in {
        val invalid = "bli bla blub"
        val result = DiagramParser.parseDiagrams(invalid)
        result.successful shouldBe false
      }

    }
  }

}
