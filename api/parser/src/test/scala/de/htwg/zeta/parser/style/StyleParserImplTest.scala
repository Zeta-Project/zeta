package de.htwg.zeta.parser.style

import javafx.scene.paint.Color
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Inside


class StyleParserImplTest extends FlatSpec with Matchers with Inside {

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

  val selfCycleIn2ndParent: String =
    """
     | style A { description = "" }
     | style B extends A, B { description = "" }
    """.stripMargin

  val trivialCycleIn2ndParent: String =
    """
     | style A { description = "" }
     | style B extends A, C { description = "" }
     | style C extends A, B { description = "" }
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

    inside(styleParser) {
      case parserToTest.Success(List(style: StyleParseTree), _) =>
        style.name shouldBe "Y"
        style.description shouldBe "\"Style for a connection between an interface and its implementing class\""

        style.attributes should contain(Transparency(1.0))
        style.attributes should contain(BackgroundColor(Color.WHITE))
        style.attributes should contain(LineColor(Color.BLACK))
        style.attributes should contain(LineStyle("dash"))
        style.attributes should contain(LineWidth(1))
        style.attributes should contain(FontColor(Color.BLACK))
        style.attributes should contain(FontName("Helvetica"))
        style.attributes should contain(FontSize(20))
        style.attributes should contain(FontBold(true))
        style.attributes should contain(FontItalic(true))
        style.attributes should contain(GradientOrientation("vertical"))
        style.attributes should contain(GradientAreaColor(Color.BLACK))
        style.attributes should contain(GradientAreaOffset(2.0))
    }
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
    duplicates.size shouldBe 2
    duplicates should contain("line-color")
    duplicates should contain("line-style")
  }

  "A StyleParser" should "list duplicate occurrences only once" in {
    val attributes = List(
      LineWidth(12),
      LineWidth(14),
      LineWidth(12),
      LineWidth(16)
    )
    val duplicates = parserToTest.findAttributeDuplicates(attributes)
    duplicates.size shouldBe 1
    duplicates should contain("line-width")
  }

  "A StyleParser" should "find no duplicates" in {
    val attributes = List(
      LineColor(Color.BLUE),
      LineWidth(12),
      LineStyle("solid")
    )
    val duplicates = parserToTest.findAttributeDuplicates(attributes)
    duplicates shouldBe Nil
  }

  "A StyleParser" should "succeed if a style has no parent style" in {
    val parseResult = parserToTest.parseStyles(styleWithoutParentStyle)
    inside(parseResult) {
      case parserToTest.Success(List(styleParseTree: StyleParseTree), _) =>
        styleParseTree.parentStyles shouldBe empty
    }

  }

  "A StyleParser" should "succeed if a style has a single parent style" in {
    val parseResult = parserToTest.parseStyles(styleWithSingleParentStyle)
    inside(parseResult) {
      case parserToTest.Success((List(parentStyle: StyleParseTree, childStyle: StyleParseTree)), _) =>
        parentStyle.parentStyles shouldBe empty
        childStyle.parentStyles should contain("ParentStyle")
    }
  }

  "A StyleParser" should "succeed if a style has multiple parent styles" in {
    val parseResult = parserToTest.parseStyles(styleWithMultipleParentStyles)
    inside(parseResult) {
      case parserToTest.Success((styles: List[StyleParseTree]), _) =>
        styles should have size 4
        inside(styles.find(s => s.name == "MyStyle")) {
          case Some(childStyle: StyleParseTree) =>
            childStyle.parentStyles should contain("ParentStyle1")
            childStyle.parentStyles should contain("ParentStyle2")
            childStyle.parentStyles should contain("ParentStyle3")
        }
    }
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

  "A StyleParser" should "find self cycle in a style that has a leading legal parent" in {
    val parseResult = parserToTest.parseStyles(selfCycleIn2ndParent)
    parseResult.successful shouldBe false
  }

  "A StyleParser" should "find cycles in a trivial cycle graph when there is a leading legal parent" in {
    val parseResult = parserToTest.parseStyles(trivialCycleIn2ndParent)
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
    styleParser.successful shouldBe false
  }

  "A StyleParser" should "fail without Braces" in {
    val styleParser = parserToTest.parseStyles(styleWithoutBraces)
    styleParser.successful shouldBe false
  }

  "A StyleParser" should "fail with duplicate Attributes" in {
    val styleParser = parserToTest.parseStyles(styleWithDuplicateAttributes)
    styleParser.successful shouldBe false
  }

  "A StyleParser" should "succeed if colors defined as gradients" in {
    val styleParser = parserToTest.parseStyles(styleWithGradientColors)
    styleParser.successful shouldBe true
  }

  "A StyleParser" should "fail if an invalid color is specified" in {
    val styleParser = parserToTest.parseStyles(styleWithInvalidColor)
    styleParser.successful shouldBe false
  }
}
