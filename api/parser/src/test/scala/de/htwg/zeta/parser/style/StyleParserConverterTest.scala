package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.model.style.{DASH, Style}
import de.htwg.zeta.server.generator.model.style.color.{BLACK, WHITE}
import de.htwg.zeta.server.generator.model.style.gradient.VERTICAL
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class StyleParserConverterTest extends FlatSpec with Matchers {

  val parserToTest: StyleParserImpl = new StyleParserImpl

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

    val styleParser = parserToTest.parseStyle(styleToTestSuccess)
    styleParser.successful shouldBe true
    val style: StyleParseModel = styleParser.get
    val deprecatedStyle: Style = StyleParserImpl.convert(style)

    deprecatedStyle.description shouldBe Some("Style for a connection between an interface and its implementing class")
    deprecatedStyle.line_width shouldBe Some(1)
    deprecatedStyle.background_color shouldBe Some(WHITE)
    deprecatedStyle.line_style shouldBe Some(DASH)
    deprecatedStyle.line_width shouldBe Some(1)
    deprecatedStyle.font_color shouldBe Some(BLACK)
    deprecatedStyle.font_name shouldBe Some("Helvetica")
    deprecatedStyle.font_size shouldBe Some(20)
    deprecatedStyle.font_bold shouldBe Some(true)
    deprecatedStyle.font_italic shouldBe Some(true)
    deprecatedStyle.gradient_orientation shouldBe Some(VERTICAL)
  }

}
