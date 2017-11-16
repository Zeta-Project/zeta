package de.htwg.zeta.parser.style

import org.scalatest.WordSpec


class StyleParserImplTest extends WordSpec {

  val styleToTestSucces:String="style Y {\n  description = \"Style for a connection between an interface and its implementing class\"\n  line-color = black\n  line-style = dash\n  line-width = 1\n  gradient-orientation = vertical\n  background-color = white\n  font-size = 20\n}"
  val styleToTestSuccesWithoutDescription:String="style Y {\n  line-color = black\n  line-style = dash\n  line-width = 1\n  gradient-orientation = vertical\n  background-color = white\n  font-size = 20\n}"
  val styleToTestFailNoBraces:String="style Y \n  description = \"Style for a connection between an interface and its implementing class\"\n  line-color = black\n  line-style = dash\n  line-width = 1\n  gradient-orientation = vertical\n  background-color = white\n  font-size = 20\n"

  val parserToTest:StyleParserImpl = new StyleParserImpl



  "A StyleParser" should {
    "succeed" in {
      val styleParser = parserToTest.parseStyle(styleToTestSucces)
      assert(styleParser.successful)
      val style: StyleParseModel = styleParser.get
      assert(style.name =="Y")
      assert(style.description.get == "\"Style for a connection between an interface and its implementing class\"")
      assert(style.lineColor == "black")
      assert(style.lineStyle == "dash")
      assert(style.lineWidth == 1)
      assert(style.gradientOrientation == "vertical")
      assert(style.backgroundColor == "white")
      assert(style.fontSize == 20)
    }
  }

  "A StyleParser" should {
    "fail without Braces" in {
      val styleParser = parserToTest.parseStyle(styleToTestFailNoBraces)
      assert(!styleParser.successful)
    }
  }

  "A StyleParser" should {
    "succeed without a description" in {
      val styleParser = parserToTest.parseStyle(styleToTestSuccesWithoutDescription)
      assert(styleParser.successful)
    }
  }
}
