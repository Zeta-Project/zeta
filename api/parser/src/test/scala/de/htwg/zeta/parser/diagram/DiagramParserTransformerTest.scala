package de.htwg.zeta.parser.diagram

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class DiagramParserTransformerTest extends FreeSpec with Matchers with Inside {

  private object NodeFactory {
    def apply(name: String): Node = Node(
      name = name,
      conceptElement = "Test",
      edges = List[Edge](),
      size = Size(0, 0, 0, 0, 0, 0),
      style = Style(
        "TestStyle",
        "TestDescription",
        Background(Color(0, 0, 0)),
        Font("TestFont", bold = false, Color(0, 0, 0), italic = false, 0),
        Line(Color(0, 0, 0), Dashed(), 1),
        1.0
      ),
      resizing = Resizing(horizontal = false, vertical = false, proportional = false),
      geoModels = List[GeoModel]()
    )
  }

  val nodes = List(NodeFactory("node0"), NodeFactory("node1"), NodeFactory("node2"))

  "A diagram transformer will find" - {

    "no errors when" - {

      "there are no duplicates" in {
        val diagrams = List(
          DiagramParseTree("diagram0", List(
            PaletteParseTree("palette0", List(
              NodeParseTree("node0"), NodeParseTree("node1"), NodeParseTree("node2")
            )),
            PaletteParseTree("palette2", List(
              NodeParseTree("node2")
            ))
          )),
          DiagramParseTree("diagram1", List(
            PaletteParseTree("palette1", List(
              NodeParseTree("node1")
            ))
          ))
        )
        val result = DiagramParseTreeTransformer.transform(diagrams, nodes)
        result.isSuccess shouldBe true

        val model1 = result.getOrElse(Nil).head
        model1.name shouldBe "diagram0"
        model1.palettes.size shouldBe 2
        model1.palettes.head.name shouldBe "palette0"
        model1.palettes.head.nodes.size shouldBe 3
        model1.palettes.head.nodes.head.name shouldBe "node0"
        model1.palettes.head.nodes(1).name shouldBe "node1"
        model1.palettes.head.nodes(2).name shouldBe "node2"
        model1.palettes(1).name shouldBe "palette2"
        model1.palettes(1).nodes.size shouldBe 1
        model1.palettes(1).nodes.head.name shouldBe "node2"

        val model2 = result.getOrElse(Nil)(1)
        model2.name shouldBe "diagram1"
        model2.palettes.size shouldBe 1
        model2.palettes.head.name shouldBe "palette1"
        model2.palettes.head.nodes.size shouldBe 1
        model2.palettes.head.nodes.head.name shouldBe "node1"
      }

      "the same node is used in different palettes" in {
        val sharedNode = NodeParseTree("node1")
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette1", List(sharedNode)),
          PaletteParseTree("palette2", List(sharedNode))
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram), nodes)
        result.isSuccess shouldBe true

        val model = result.getOrElse(Nil).head
        model.name shouldBe "diagram"
        model.palettes.size shouldBe 2
        model.palettes.head.name shouldBe "palette1"
        model.palettes.head.nodes.size shouldBe 1
        model.palettes.head.nodes.head.name shouldBe "node1"
        model.palettes(1).name shouldBe "palette2"
        model.palettes(1).nodes.size shouldBe 1
        model.palettes(1).nodes.head.name shouldBe "node1"
      }

      "the same palette is used in different diagrams" in {
        val sharedPalette = PaletteParseTree("palette1", List())
        val diagrams = List(
          DiagramParseTree("diagram0", List(sharedPalette)),
          DiagramParseTree("diagram1", List(sharedPalette))
        )
        val result = DiagramParseTreeTransformer.transform(diagrams, nodes)
        result.isSuccess shouldBe true

        val model1 = result.getOrElse(Nil).head
        model1.name shouldBe "diagram0"
        model1.palettes.size shouldBe 1
        model1.palettes.head.name shouldBe "palette1"
        model1.palettes.head.nodes.size shouldBe 0

        val model2 = result.getOrElse(Nil)(1)
        model2.name shouldBe "diagram1"
        model2.palettes.size shouldBe 1
        model2.palettes.head.name shouldBe "palette1"
        model2.palettes.head.nodes.size shouldBe 0
      }
    }
    "errors when" - {

      "there are duplicated diagrams" in {
        val diagrams = List(
          DiagramParseTree("diagram", List()),
          DiagramParseTree("diagram", List())
        )
        val result = DiagramParseTreeTransformer.transform(diagrams, nodes)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following diagram is defined multiple times: diagram")
      }

      "there are duplicated palettes within a single diagram" in {
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette", List()),
          PaletteParseTree("palette", List())
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram), nodes)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following palettes are defined multiple times: palette")
      }

      "there are duplicated nodes within a single palette" in {
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette", List(
            NodeParseTree("node0"),
            NodeParseTree("node0")
          ))
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram), nodes)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following node is defined multiple times: node0")
      }

      "there are referenced nodes which does not exists" in {
        val diagram = DiagramParseTree("diagram", List(
          PaletteParseTree("palette", List(
            NodeParseTree("node5")
          ))
        ))
        val result = DiagramParseTreeTransformer.transform(List(diagram), nodes)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following nodes are not defined in shape: node5")
      }

    }
  }
}
