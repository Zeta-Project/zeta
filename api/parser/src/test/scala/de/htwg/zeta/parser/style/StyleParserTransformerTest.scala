package de.htwg.zeta.parser.style

import de.htwg.zeta.common.model.style.Color
import de.htwg.zeta.common.model.style.Dashed
import org.scalatest.FreeSpec
import org.scalatest.Matchers

class StyleParserTransformerTest extends FreeSpec with Matchers {

  val parser: StyleParserImpl = new StyleParserImpl

  "A style transformer will" - {
    "transform without errors" - {
      "when building a valid style" in {
        val styleToTestSuccess: String =
          """
           |style Y {
           |  description = "Style for a connection between an interface and its implementing class"
           |  transparency = 1.0
           |  background-color = white
           |  line-color = black
           |  line-style = dash
           |  line-width = 1
           |  font-color = black
           |  font-name = Helvetica
           |  font-size = 20
           |  font-bold = true
           |  font-italic = true
           |  gradient-orientation = vertical
           |  gradient-area-color = black
           |  gradient-area-offset = 2.0
           |}
          """.stripMargin

        val styleParser = parser.parseStyles(styleToTestSuccess)
        styleParser.successful shouldBe true

        StyleParseTreeTransformer.transform(styleParser.get).getOrElse(List())
          .headOption.fold(fail())(style => {
          style.name shouldBe "Y"
          style.description shouldBe "\"Style for a connection between an interface and its implementing class\""
          style.background.color shouldBe Color(255, 255, 255)
          style.font.name shouldBe "Helvetica"
          style.font.bold shouldBe true
          style.font.color shouldBe Color(0, 0, 0)
          style.font.italic shouldBe true
          style.font.size shouldBe 20
          style.font.transparent shouldBe false
          style.line.color shouldBe Color(0, 0, 0)
          style.line.style shouldBe Dashed()
          style.line.transparent shouldBe false
          style.line.width shouldBe 1
          style.transparency shouldBe 1.0
        })
      }

      "when building a valid style with different colors" in {
        val styleToTestColors: String =
          """
           |style Y {
           |  description = "Style for a connection between an interface and its implementing class"
           |  background-color = orange
           |  line-color = gray
           |  font-color = green
           |}
          """.stripMargin

        val styleParser = parser.parseStyles(styleToTestColors)
        styleParser.successful shouldBe true

        StyleParseTreeTransformer.transform(styleParser.get).getOrElse(List())
          .headOption.fold(fail())(style => {
          style.background.color shouldBe Color(255, 165, 0)
          style.line.color shouldBe Color(128, 128, 128)
          style.font.color shouldBe Color(0, 128, 0)
        })
      }
    }
    "find errors when" - {
      "transforming styles which are duplicated" in {
        val styleToTestGraphCycle = List(
          StyleParseTree("style1", "test", List("style3"), List()),
          StyleParseTree("style1", "test", List("style3"), List()),
          StyleParseTree("style3", "test", List(), List())
        )

        val result = StyleParseTreeTransformer.transform(styleToTestGraphCycle)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors should contain("The following styles are defined multiple times: style1")
      }

      "transforming styles with undefined parents" in {
        val styleToTestGraphCycle = List(
          StyleParseTree("style1", "test", List("style2"), List()),
          StyleParseTree("style2", "test", List("style4"), List()),
          StyleParseTree("style3", "test", List("style4"), List())
        )

        val result = StyleParseTreeTransformer.transform(styleToTestGraphCycle)
        result.isSuccess shouldBe false
        val errors = result.toEither.left.get
        errors should contain("The following styles are referenced as parent but not defined: style4")
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
        errors should contain("The following styles defines a graph circle with its parent styles: style1,style2,style3")
      }
    }
  }

}
