package de.htwg.zeta.parser.diagram

import org.scalatest.{FreeSpec, Inside, Matchers}

//noinspection ScalaStyle
class DiagramParserTest extends FreeSpec with Matchers with Inside {

  "A diagram parser will" - {

    "succeed in parsing" - {

      "a diagram without palettes" in {
        val diagramWithoutPalettes = "diagram myDiagram { }"
        val result = DiagramParser.parseDiagrams(diagramWithoutPalettes)
        result.successful shouldBe true
        val tree = result.get
        tree.size shouldBe 1
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
        tree.size shouldBe 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes.size shouldBe 1
        val palette = diagram.palettes.head
        palette.name shouldBe "myPalette"
        palette.nodes shouldBe empty
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
        tree.size shouldBe 1
        val diagram = tree.head
        diagram.name shouldBe "myDiagram"
        diagram.palettes.size shouldBe 3
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


    }
  }

}
