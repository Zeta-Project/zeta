package de.htwg.zeta.parser.diagram

import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class DiagramParserTransformerTest  extends FreeSpec with Matchers with Inside {

  "A diagram transformer will find" - {

    "no errors when" - {

      "there are no duplicates" in {
        val diagrams = List(
          DiagramParseTree("diagram0", List(
            PaletteParseTree("palette0", List(
              NodeParseTree("node0"), NodeParseTree("node1"), NodeParseTree("node2")
            )),
            PaletteParseTree("palette2", List(
              NodeParseTree("node3")
            ))
          )),
          DiagramParseTree("diagram1", List(
            PaletteParseTree("palette1", List(
              NodeParseTree("node4")
            ))
          ))
        )
        val result = DiagramParseTreeTransformer.transform(diagrams)
        result.isSuccess shouldBe true
      }

      "the same node is used in different palettes" in {
        val sharedNode = NodeParseTree("shared")
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette1", List(sharedNode)),
          PaletteParseTree("palette2", List(sharedNode))
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram))
        result.isSuccess shouldBe true
      }

      "the same palette is used in different diagrams" in {
        val sharedPalette = PaletteParseTree("shared", List())
        val diagrams = List(
          DiagramParseTree("diagram0", List(sharedPalette)),
          DiagramParseTree("diagram1", List(sharedPalette))
        )
        val result = DiagramParseTreeTransformer.transform(diagrams)
        result.isSuccess shouldBe true
      }
    }
    "errors when" - {

      "there are duplicated diagrams" in {
        val diagrams = List(
          DiagramParseTree("diagram", List()),
          DiagramParseTree("diagram", List())
        )
        val result = DiagramParseTreeTransformer.transform(diagrams)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors should contain("The following diagrams are defined multiple times: diagram")
      }

      "there are duplicated palettes within a single diagram" in {
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette", List()),
          PaletteParseTree("palette", List())
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram))
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors should contain("The following palettes are defined multiple times: palette")
      }

      "there are duplicated nodes within a single palette" in {
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette", List(
            NodeParseTree("node"),
            NodeParseTree("node")
          ))
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram))
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors should contain("The following nodes are defined multiple times: node")
      }

    }
  }
}
