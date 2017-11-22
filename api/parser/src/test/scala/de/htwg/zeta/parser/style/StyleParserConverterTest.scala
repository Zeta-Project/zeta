package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.model.style.Style
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

    print(deprecatedStyle.line_color.toString)
    // TODO make test

  }

}
