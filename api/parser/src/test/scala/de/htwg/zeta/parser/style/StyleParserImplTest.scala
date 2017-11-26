package de.htwg.zeta.parser.style

import javafx.scene.paint.Color

import org.scalatest.{FlatSpec, Matchers}


class StyleParserImplTest extends FlatSpec with Matchers {

  val parserToTest: StyleParserImpl = new StyleParserImpl

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
      |style ParentStyle { description = "Parent style" }
      |style ChildStyle extends ParentStyle {
      |  description = "Style which extends a single parent style"
      |}
    """.stripMargin

  val styleWithMultipleParentStyles =
    """
      |style ParentStyle1 { description = "Parent Style1" }
      |style ParentStyle2 { description = "Parent Style2" }
      |style ParentStyle3 { description = "Parent Style3" }
      |style MyStyle extends ParentStyle1, ParentStyle2, ParentStyle3 {
      |  description = "Style which extends multiple parent styles"
      |}
    """.stripMargin

  val styleWithUndefinedParentStyle =
    """
      |style MyStyle extends NoSuchParentStyleDefined {
      |  description = "Style with extends an undefined parent style"
      |}
    """.stripMargin

  val styleWithInvalidParentStyle =
    """
      |style MyStyle extends {
      |  description = "This style uses the 'extends' keyword but does not specify a parent style"
      |}
    """.stripMargin

  val styleWhichExtendsItself =
    """
      |style MyStyle extends MyStyle {
      |  description = "This style extends itself which is forbidden"
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

  val styleWithInvalidColor: String =
    """
      |style StyleWithInvalidColor {
      |  description = "Gherkins are green but not a valid color"
      |  background-color = Gherkins
      |}
    """.stripMargin

  val trivialCycle: String =
    """
      | style A extends B { description = "" }
      | style B extends A { description = "" }
    """.stripMargin

  val triangleCycle: String =
    """
      | style A extends B { description = "" }
      | style B extends C { description = "" }
      | style C extends A { description = "" }
    """.stripMargin

  val acyclicGraph: String =
    """
      | style Parent { description = "" }
      | style A extends Parent { description = "" }
      | style B extends Parent { description = "" }
    """.stripMargin

  val diamondGraph: String =
    """
      | style A { description = "" }
      | style B1 extends A { description = "" }
      | style B2 extends A { description = "" }
      | style C extends B1, B2 { description = "" }
    """.stripMargin

  val diamondGraphWithCycles: String =
    """
      | style A  extends C { description = "" }
      | style B1 extends A { description = "" }
      | style B2 extends A { description = "" }
      | style C extends B1, B2 { description = "" }
    """.stripMargin

  "A StyleParser" should "succeed" in {
    val styleParser = parserToTest.parseStyles(styleToTestSuccess)
    assert(styleParser.successful)
    val style: StyleParseModel = styleParser.get.head

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
  }

  "A StyleParser" should "find duplicate attributes and fail" in {
    val attributes = List(
      LineColor(Color.BLUE),
      LineWidth(12),
      LineStyle("solid"),
      LineColor(Color.RED),
      LineStyle("dash")
    )
    val duplicates = parserToTest.findAttributeDuplicates(attributes)
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
    val duplicates = parserToTest.findAttributeDuplicates(attributes)
    assert(duplicates.size == 1)
    assert(duplicates.head.equals("line-width"))
  }

  "A StyleParser" should "find no duplicates" in {
    val attributes = List(
      LineColor(Color.BLUE),
      LineWidth(12),
      LineStyle("solid")
    )
    val duplicates = parserToTest.findAttributeDuplicates(attributes)
    assert(duplicates.isEmpty)
  }

  "A StyleParser" should "succeed if a style has no parent style" in {
    val parseResult = parserToTest.parseStyles(styleWithoutParentStyle)
    assert(parseResult.successful)
    val styleParseModel = parseResult.get.head
    assert(styleParseModel.parentStyles.isEmpty)
  }

  "A StyleParser" should "succeed if a style has a single parent style" in {
    val parseResult = parserToTest.parseStyles(styleWithSingleParentStyle)
    assert(parseResult.successful)

    val parentStyle = parseResult.get.head
    parentStyle.parentStyles shouldBe empty

    val childStyle = parseResult.get(1)
    childStyle.parentStyles.length shouldBe 1
    childStyle.parentStyles.head shouldBe "ParentStyle"
  }

  "A StyleParser" should "succeed if a style has multiple parent styles" in {
    val parseResult = parserToTest.parseStyles(styleWithMultipleParentStyles)
    parseResult.successful shouldBe true
    val parentStyle1 = parseResult.get.head
    val parentStyle2 = parseResult.get(1)
    val parentStyle3 = parseResult.get(2)
    val childStyle = parseResult.get(3)
    childStyle.parentStyles.size shouldBe 3
    childStyle.parentStyles.head shouldBe "ParentStyle1"
    childStyle.parentStyles(1) shouldBe "ParentStyle2"
    childStyle.parentStyles(2) shouldBe "ParentStyle3"
  }

  "A StyleParser" should "fail if a parent style is undefined" in {
    val parseResult = parserToTest.parseStyles(styleWithUndefinedParentStyle)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "fail if a style extends itself" in {
    val parseResult = parserToTest.parseStyles(styleWhichExtendsItself)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "fail if a style specifies an invalid style extension" in {
    val parseResult = parserToTest.parseStyles(styleWithInvalidParentStyle)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "find cycles in a trivial cycle graph" in {
    val parseResult = parserToTest.parseStyles(trivialCycle)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "find cycles in a triangle cycle graph" in {
    val parseResult = parserToTest.parseStyles(triangleCycle)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "find no cycles in an acyclic graph" in {
    val parseResult = parserToTest.parseStyles(acyclicGraph)
    parseResult.successful shouldBe true
  }

  "A StyleParser" should "find no cycles in a diamond graph" in {
    val parseResult = parserToTest.parseStyles(diamondGraph)
    parseResult.successful shouldBe true
  }

  "A StyleParser" should "find cycles in a diamond graph with cycles" in {
    val parseResult = parserToTest.parseStyles(diamondGraphWithCycles)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "fail without a description" in {
    val styleParser = parserToTest.parseStyles(styleWithoutDescription)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "fail without Braces" in {
    val styleParser = parserToTest.parseStyles(styleWithoutBraces)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "fail with duplicate Attributes" in {
    val styleParser = parserToTest.parseStyles(styleWithDuplicateAttributes)
    assert(!styleParser.successful)
  }

  "A StyleParser" should "succeed if colors defined as gradients" in {
    val styleParser = parserToTest.parseStyles(styleWithGradientColors)
    assert(styleParser.successful)
  }

  "A StyleParser" should "fail if an invalid color is specified" in {
    val styleParser = parserToTest.parseStyles(styleWithInvalidColor)
    styleParser.successful shouldBe false
  }
}
