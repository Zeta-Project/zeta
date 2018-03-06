package de.htwg.zeta.parser.style

import de.htwg.zeta.common.model.style.Color
import de.htwg.zeta.common.model.style.Dashed
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class StyleParserTransformerTest extends FlatSpec with Matchers {

  val parser: StyleParserImpl = new StyleParserImpl

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

  "A converter" should "build a style" in {
    val styleParser = parser.parseStyles(styleToTestSuccess)
    styleParser.successful shouldBe true

    val style = StyleParseTreeTransformer.transform(styleParser.get).getOrElse(List()).head
    style.name shouldBe "Y"
    style.description shouldBe "\"Style for a connection between an interface and its implementing class\""
    style.background.color shouldBe Color("255,255,255")
    style.font.name shouldBe "Helvetica"
    style.font.bold shouldBe true
    style.font.color shouldBe Color("0,0,0")
    style.font.italic shouldBe true
    style.font.size shouldBe 20
    style.font.transparent shouldBe false
    style.line.color shouldBe Color("0,0,0")
    style.line.style shouldBe Dashed()
    style.line.transparent shouldBe false
    style.line.width shouldBe 1
    style.transparency shouldBe 1.0
  }

  val styleToTestColors: String =
    """
     |style Y {
     |  description = "Style for a connection between an interface and its implementing class"
     |  background-color = orange
     |  line-color = gray
     |  font-color = green
     |}
    """.stripMargin

  "A converter" should "build a style with special colors" in {
    val styleParser = parser.parseStyles(styleToTestColors)
    styleParser.successful shouldBe true

    val style = StyleParseTreeTransformer.transform(styleParser.get).getOrElse(List()).head
    style.background.color shouldBe Color("255,165,0")
    style.line.color shouldBe Color("128,128,128")
    style.font.color shouldBe Color("0,128,0")
  }

}
