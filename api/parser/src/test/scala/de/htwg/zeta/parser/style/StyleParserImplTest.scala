package de.htwg.zeta.parser.style

import org.scalatest.FlatSpec


class StyleParserImplTest extends FlatSpec {

  val styleToTestSucces =
    """
     |style Y {
     |  description = "Style for a connection between an interface and its implementing class"
     |  line-color = black
     |  line-style = dash
     |  line-width = 1
     |  gradient-orientation = vertical
     |  background-color = white
     |  font-size = 20
     |}
    """.stripMargin

  val styleToTestSucces2 =
    """
     |style
     |Y
     |{
     | description = "Style for a connection between an interface and its implementing class"
     | line-color = black
     | line-style = dash
     |  line-width = 1
     |  gradient-orientation = vertical
     |  background-color = white
     |  font-size = 20
     |}
    """.stripMargin


  val styleToTestSuccesWithoutDescription: String = "style Y {\n  line-color = black\n  line-style = dash\n  line-width = 1\n  gradient-orientation = vertical\n  background-color = white\n  font-size = 20\n}"
  val styleToTestFailNoBraces: String = "style Y \n  description = \"Style for a connection between an interface and its implementing class\"\n  line-color = black\n  line-style = dash\n  line-width = 1\n  gradient-orientation = vertical\n  background-color = white\n  font-size = 20\n"

  val parserToTest: StyleParserImpl = new StyleParserImpl


  "A StyleParser" should "succeed" in {
    val styleParser = parserToTest.parseStyle(styleToTestSucces)
    assert(styleParser.successful)
    val style: StyleParseModel = styleParser.get
    assert(style.name == "Y")
    /*assert(style.description.get == "\"Style for a connection between an interface and its implementing class\"")
    assert(style.lineColor == "black")
    assert(style.lineStyle == "dash")
    assert(style.lineWidth == 1)
    assert(style.gradientOrientation == "vertical")
    assert(style.backgroundColor == "white")
    assert(style.fontSize == 20)*/
  }


  "A StyleParser" should "fail without Braces" in {
    val styleParser = parserToTest.parseStyle(styleToTestFailNoBraces)
    assert(!styleParser.successful)
  }


  "A StyleParser" should "succeed without a description" in {
    val styleParser = parserToTest.parseStyle(styleToTestSuccesWithoutDescription)
    assert(styleParser.successful)
  }

  "A StyleParser" should "find duplicate attributes" in {
    val attributes = List(
      LineColor("blue"),
      LineWidth(12),
      LineStyle("solid"),
      LineColor("red"),
      LineStyle("dash")
    )
    val duplicates = parserToTest.findDuplicates(attributes)
    assert(duplicates.size == 2)
    assert(duplicates.contains("line-color"))
    assert(duplicates.contains("line-style"))
  }

  "A StyleParser" should "list duplicate occurrences only once" in {
    val attributes = List(
      LineWidth(12),
      LineWidth(14),
      LineWidth(12),
      LineWidth(16)
    )
    val duplicates = parserToTest.findDuplicates(attributes)
    assert(duplicates.size == 1)
    assert(duplicates.head.equals("line-width"))
  }

  "A StyleParser" should "find no duplicates" in {
    val attributes = List(
      LineColor("blue"),
      LineWidth(12),
      LineStyle("solid")
    )
    val duplicates = parserToTest.findDuplicates(attributes)
    assert(duplicates.isEmpty)
  }

}

