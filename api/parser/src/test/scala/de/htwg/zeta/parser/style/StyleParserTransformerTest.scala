package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.model.style.{DASH, Style}
import de.htwg.zeta.server.generator.model.style.gradient.VERTICAL
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class StyleParserTransformerTest extends FlatSpec with Matchers {

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

  val styleToTestColors: String =
    """
      |style Y {
      |  description = "Style for a connection between an interface and its implementing class"
      |  background-color = orange
      |  line-color = gray
      |  font-color = green
      |}
    """.stripMargin

  "A converter" should "build a style" in {
    val styleParser = parserToTest.parseStyles(styleToTestSuccess)
    styleParser.successful shouldBe true
    val styleTrees = styleParser.get
    val styles = StyleParseTreeTransformer.transform(styleTrees).getOrElse(List())
    val deprecatedStyle: Style = styles.head

    deprecatedStyle.description shouldBe Some("\"Style for a connection between an interface and its implementing class\"")
    deprecatedStyle.line_width shouldBe Some(1)
    deprecatedStyle.background_color.map(_.getRGBValue) shouldBe Some("255,255,255")
    deprecatedStyle.line_style shouldBe Some(DASH)
    deprecatedStyle.line_width shouldBe Some(1)
    deprecatedStyle.font_color.map(_.getRGBValue) shouldBe Some("0,0,0")
    deprecatedStyle.font_name shouldBe Some("Helvetica")
    deprecatedStyle.font_size shouldBe Some(20)
    deprecatedStyle.font_bold shouldBe Some(true)
    deprecatedStyle.font_italic shouldBe Some(true)
    deprecatedStyle.gradient_orientation shouldBe Some(VERTICAL)
  }

  "A converter" should "build a style with special colors" in {
    val styleParser = parserToTest.parseStyles(styleToTestColors)
    styleParser.successful shouldBe true
    val styleTrees = styleParser.get
    val styles = StyleParseTreeTransformer.transform(styleTrees).getOrElse(List())
    val deprecatedStyle: Style = styles.head

    deprecatedStyle.background_color.map(_.getRGBValue) shouldBe Some("255,165,0")
    deprecatedStyle.line_color.map(_.getRGBValue) shouldBe Some("128,128,128")
    deprecatedStyle.font_color.map(_.getRGBValue) shouldBe Some("0,128,0")
  }

}
