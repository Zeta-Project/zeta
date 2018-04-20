package de.htwg.zeta.parser.style

import javafx.scene.paint

import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers

//noinspection ScalaStyle
class StyleParserTransformerTest extends FreeSpec with Matchers {

  "A style transformer will" - {
    "transform without errors" - {
      "when building a valid style" in {
        val styleToTestSuccess = List(
          StyleParseTree("Y", "\"Style for a connection between an interface and its implementing class\"", List(), List(
            Transparency(1.0),
            BackgroundColor(paint.Color.valueOf("white")),
            LineColor(paint.Color.valueOf("black")),
            LineStyle("dash"),
            LineWidth(1),
            FontColor(paint.Color.valueOf("black")),
            FontName("Helvetica"),
            FontSize(20),
            FontBold(true),
            FontItalic(true),
            GradientOrientation("vertical"),
            GradientAreaColor(paint.Color.valueOf("black")),
            GradientAreaOffset(2.0)
          ))
        )

        val result = StyleParseTreeTransformer.transform(styleToTestSuccess)
        result.isSuccess shouldBe true

        val styles = result.toOption.get
        styles.size shouldBe 2
        val defaultStyle = styles.head
        defaultStyle shouldBe Style.defaultStyle
        val style = styles(1)
        style.name shouldBe "Y"
        style.description shouldBe "\"Style for a connection between an interface and its implementing class\""
        style.background.color shouldBe Color(255, 255, 255, 1)
        style.font.name shouldBe "Helvetica"
        style.font.bold shouldBe true
        style.font.color shouldBe Color(0, 0, 0, 1)
        style.font.italic shouldBe true
        style.font.size shouldBe 20
        style.line.color shouldBe Color(0, 0, 0, 1)
        style.line.style shouldBe Dashed()
        style.line.width shouldBe 1
        style.transparency shouldBe 1.0
      }

      "when building a valid style with different colors" in {
        val styleToTestColors = List(
          StyleParseTree("Y", "Style for a connection between an interface and its implementing class", List(), List(
            BackgroundColor(paint.Color.valueOf("orange")),
            LineColor(paint.Color.valueOf("gray")),
            FontColor(paint.Color.valueOf("green"))
          ))
        )
        val result = StyleParseTreeTransformer.transform(styleToTestColors)
        result.isSuccess shouldBe true

        val styles = result.toOption.get
        styles.size shouldBe 2
        val defaultStyle = styles.head
        defaultStyle shouldBe Style.defaultStyle
        val style = styles(1)
        style.background.color shouldBe Color(255, 165, 0, 1)
        style.line.color shouldBe Color(128, 128, 128, 1)
        style.font.color shouldBe Color(0, 128, 0, 1)
      }
    }
    "find errors when" - {
      "transforming styles which are duplicated" in {
        val styleToTestDuplicates = List(
          StyleParseTree("style1", "test", List("style3"), List()),
          StyleParseTree("style1", "test", List("style3"), List()),
          StyleParseTree("style3", "test", List(), List())
        )

        val result = StyleParseTreeTransformer.transform(styleToTestDuplicates)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following styles are defined multiple times: style1")
      }

      "transforming styles with undefined parents" in {
        val styleToTestUndefinedParents = List(
          StyleParseTree("style1", "test", List("style2"), List()),
          StyleParseTree("style2", "test", List("style4"), List()),
          StyleParseTree("style3", "test", List("style4"), List())
        )

        val result = StyleParseTreeTransformer.transform(styleToTestUndefinedParents)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following style is referenced as parent but not defined: style4")
      }

      "transforming styles with cycling dependencies" in {
        val styleToTestGraphCycle = List(
          StyleParseTree("style1", "test", List("style2"), List()),
          StyleParseTree("style2", "test", List("style3"), List()),
          StyleParseTree("style3", "test", List("style1"), List())
        )

        val result = StyleParseTreeTransformer.transform(styleToTestGraphCycle)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors.size shouldBe 1
        errors should contain("The following styles define a graph circle with its parent styles: style1, style2, style3")
      }
    }
    "inherit style values" - {
      "when s single style is extended" in {
        val styles = List(
          StyleParseTree("A", "A", List(), List(
            BackgroundColor(paint.Color.valueOf("green")),
            FontSize(20)
          )),
          StyleParseTree("B", "B", List("A"), List())
        )
        val result = StyleParseTreeTransformer.transform(styles)
        result.isSuccess shouldBe true
        val styleA = result.toOption.get(1)
        styleA.name shouldBe "A"
        styleA.background.color shouldBe Color(0, 128, 0, 1)
        styleA.font.size shouldBe 20
        val styleB = result.toOption.get(2)
        styleB.name shouldBe "B"
        styleB.background.color shouldBe Color(0, 128, 0, 1)
        styleB.font.size shouldBe 20
      }
    }
  }

}
