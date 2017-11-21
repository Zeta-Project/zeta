package de.htwg.zeta.parser.style

import javafx.scene.paint.Color

import org.scalatest.FlatSpec

class StyleParserImplTest extends FlatSpec {

  val parserToTest: StyleParserImpl = new StyleParserImpl

  // todo: tempTest: missing implementations for some attributes
  val styleToTestSuccess =
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

  //  val styleToTestSuccess =
  //    """
  //      |style Y {
  //      |  description = "Style for a connection between an interface and its implementing class"
  //      |  transparency = 1.0
  //      |  background-color = white
  //      |  line-color = black
  //      |  line-style = dash
  //      |  line-width = 1
  //      |  font-color = black
  //      |  font-name = Helvetica
  //      |  font-size = 20
  //      |  font-bold = true
  //      |  font-italic = true
  //      |  gradient-orientation = vertical
  //      |  gradient-area-color = black
  //      |  gradient-area-offset = 2.0
  //      |  selected = yellow
  //      |  multiselected = orange
  //      |  allowed = green
  //      |  unallowed = red
  //      |}
  //    """.stripMargin

  val styleToTestSuccess2 =
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

  val styleWithoutParentStyle =
    """
      |style MyStyle {
      | description = "Style without parent styles"
      |}
    """.stripMargin

  val styleWithSingleParentStyle =
    """
      |style MyStyle extends ParentStyle {
      |  description = "Style which extends a single parent style"
      |}
    """.stripMargin

  val styleWithMultipleParentStyles =
    """
      |style MyStyle extends ParentStyle1, ParentStyle2, ParentStyle3 {
      |  description = "Style which extends multiple parent styles"
      |}
    """.stripMargin

  val styleWithInvalidParentStyle =
    """
      |style MyStyle extends {
      |  description = "This style uses the 'extends' keyword but does not specify a parent style"
      |}
    """.stripMargin

  val styleWithoutDescription: String =
    """
      |style Y {
      |  line-color = black
      |  line-style = dash
      |  line-width = 1
      |  gradient-orientation = vertical
      |  background-color = white
      |  font-size = 20
      |}"""

  val styleWithoutBraces: String =
    """
      |style Y
      |  description = "Style for a connection between an interface and its implementing class"
      |  line-color = black
      |  line-style = dash
      |  line-width = 1
      |  gradient-orientation = vertical
      |  background-color = white
      |  font-size = 20
      |"""

  val styleWithDuplicateAttributes: String =
    """
      |style MyStyle{
      |  description = "This style uses the 'extends' keyword but does not specify a parent style"
      |  background-color = #ffffff
      |  background-color = #000000
      |  }
    """.stripMargin

  val styleWithGradientColors: String =
    """
      |style MyStyle{
      |  description = "This style uses the 'extends' keyword but does not specify a parent style"
      |  background-color = #ffffff
      |  line-color = #000000
      |  }
    """.stripMargin

  "A StyleParser" should "succeed" in {
    val styleParser = parserToTest.parseStyle(styleToTestSuccess)
    assert(styleParser.successful)
    val style: StyleParseModel = styleParser.get
    assert(style.name == "Y")
    assert(style.description == "\"Style for a connection between an interface and its implementing class\"")

    assert(style.attributes(0).attributeName == "transparency")
    assert(style.attributes(0).asInstanceOf[Transparency].transparency == 1.0)

    assert(style.attributes(1).attributeName == "background-color")
    assert(style.attributes(1).asInstanceOf[BackgroundColor].color == Color.WHITE)

    assert(style.attributes(2).attributeName == "line-color")
    assert(style.attributes(2).asInstanceOf[LineColor].color == Color.BLACK)

    assert(style.attributes(3).attributeName == "line-style")
    assert(style.attributes(3).asInstanceOf[LineStyle].style == "dash")

    assert(style.attributes(4).attributeName == "line-width")
    assert(style.attributes(4).asInstanceOf[LineWidth].width == 1)

    assert(style.attributes(5).attributeName == "font-color")
    assert(style.attributes(5).asInstanceOf[FontColor].color == Color.BLACK)

    assert(style.attributes(6).attributeName == "font-name")
    assert(style.attributes(6).asInstanceOf[FontName].name == "Helvetica")

    assert(style.attributes(7).attributeName == "font-size")
    assert(style.attributes(7).asInstanceOf[FontSize].size == 20)

    assert(style.attributes(8).attributeName == "font-bold")
    assert(style.attributes(8).asInstanceOf[FontBold].bold == true)

    assert(style.attributes(9).attributeName == "font-italic")
    assert(style.attributes(9).asInstanceOf[FontItalic].italic == true)

    assert(style.attributes(10).attributeName == "gradient-orientation")
    assert(style.attributes(10).asInstanceOf[GradientOrientation].orientation == "vertical")

    assert(style.attributes(11).attributeName == "gradient-area-color")
    assert(style.attributes(11).asInstanceOf[GradientAreaColor].color == Color.BLACK)

    assert(style.attributes(12).attributeName == "gradient-area-offset")
    assert(style.attributes(12).asInstanceOf[GradientAreaOffset].offset == 2.0)

    //    assert(style.attributes(13).attributeName == "selected")
    //    assert(style.attributes(13).asInstanceOf[Selected].selected == "yellow")
    //
    //    assert(style.attributes(14).attributeName == "multiselected")
    //    assert(style.attributes(14).asInstanceOf[MultiSelected].multiSelected == "orange")
    //
    //    assert(style.attributes(15).attributeName == "allowed")
    //    assert(style.attributes(15).asInstanceOf[Allowed].allowed == "green")
    //
    //    assert(style.attributes(16).attributeName == "unallowed")
    //    assert(style.attributes(16).asInstanceOf[UnAllowed].unAllowed == "red")

  }

  "A StyleParser" should "find duplicate attributes and fail" in {
    val attributes = List(
      LineColor(Color.BLUE),
      LineWidth(12),
      LineStyle("solid"),
      LineColor(Color.RED),
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
      LineColor(Color.BLUE),
      LineWidth(12),
      LineStyle("solid")
    )
    val duplicates = parserToTest.findDuplicates(attributes)
    assert(duplicates.isEmpty)
  }

  "A StyleParser" should "succeed if a style has no parent style" in {
    val parseResult = parserToTest.parseStyle(styleWithoutParentStyle)
    assert(parseResult.successful)
    val styleParseModel = parseResult.get
    assert(styleParseModel.parentStyles.isEmpty)
  }

  "A StyleParser" should "succeed if a style has a single parent style" in {
    val parseResult = parserToTest.parseStyle(styleWithSingleParentStyle)
    assert(parseResult.successful)
    val styleParseModel = parseResult.get
    assert(styleParseModel.parentStyles.size == 1)
    assert(styleParseModel.parentStyles.head.equals("ParentStyle"))
  }

  "A StyleParser" should "succeed if a style has multiple parent styles" in {
    val parseResult = parserToTest.parseStyle(styleWithMultipleParentStyles)
    assert(parseResult.successful)
    val styleParseModel = parseResult.get
    assert(styleParseModel.parentStyles.size == 3)
    assert(styleParseModel.parentStyles.head.equals("ParentStyle1"))
    assert(styleParseModel.parentStyles(1).equals("ParentStyle2"))
    assert(styleParseModel.parentStyles(2).equals("ParentStyle3"))
  }

  "A StyleParser" should "fail if a style specifies an invalid style extension" in {
    val parseResult = parserToTest.parseStyle(styleWithInvalidParentStyle)
    assert(!parseResult.successful)
  }

  "A StyleParser" should "fail without a description" in {
    val styleParser = parserToTest.parseStyle(styleWithoutDescription)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "fail without Braces" in {
    val styleParser = parserToTest.parseStyle(styleWithoutBraces)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "fail with duplicate Attributes" in {
    val styleParser = parserToTest.parseStyle(styleWithDuplicateAttributes)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "succeed if colors defined as gradients" in {
    val styleParser = parserToTest.parseStyle(styleWithGradientColors)
    assert(styleParser.successful)
  }
}
