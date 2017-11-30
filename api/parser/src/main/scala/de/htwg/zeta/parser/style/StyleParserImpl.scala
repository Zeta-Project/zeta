package de.htwg.zeta.parser.style

import javafx.scene.paint.Color

import de.htwg.zeta.server.generator.model.style.color.{ColorOrGradient, ColorWithTransparency, Color => OldColor}
import de.htwg.zeta.server.generator.model.style.gradient.GradientAlignment
import de.htwg.zeta.server.generator.model.style.{Style, LineStyle => OldLineStyle}

import scala.annotation.tailrec
import scala.reflect.ClassTag


class StyleParserImpl extends StyleParser {

  private val leftBraces = literal("{")
  private val rightBraces = literal("}")
  private val eq = literal("=")
  private val comma = literal(",")

  override def styles: Parser[List[StyleParseTree]] = {
    rep1(style).flatMap { styles =>
      Parser { in =>
        checkStyleRuleViolations(styles) match {
          case Nil => Success(styles, in)
          case styleRuleViolations => failureStyleRuleViolations(styleRuleViolations, in)
        }
      }
    }
  }

  private def checkStyleRuleViolations(styles: List[StyleParseTree]): List[String] = {
    val checks = List[List[StyleParseTree] => List[String]](
      findStyleDuplicates,
      findUndefinedParents,
      findGraphCycles
    )
    checks.flatMap(_.apply(styles))
  }

  private def findStyleDuplicates(styles: List[StyleParseTree]): List[String] = {
    val styleNames = styles.map(_.name)
    val duplicates = styleNames.diff(styleNames.distinct)
    duplicates.map(styleName => s"Style '$styleName' is defined multiple times (which is forbidden)")
  }

  private def findUndefinedParents(styles: List[StyleParseTree]): List[String] = {
    val definedStyles = styles.map(_.name)
    styles.flatMap(style => {
      style.parentStyles.filter(parentStyle => !definedStyles.contains(parentStyle))
        .map(undefinedStyle => s"Style '${style.name}' extends unknown style '$undefinedStyle'")
    })
  }

  private def findGraphCycles(styles: List[StyleParseTree]): List[String] = {
    for {
      style <- styles if containsGraphCycles(style, styles)
    } yield s"Found cyclic inheritance in style ${style.name}"
  }

  private def containsGraphCycles(style: StyleParseTree, allStyles: List[StyleParseTree]): Boolean = {
    val visited = collection.mutable.Set[String]()

    @tailrec
    def check(parentNames: List[String]): Boolean = parentNames match {
      case Nil => false
      case _ if parentNames.contains(style.name) => true
      case _ =>
        val unvisited = parentNames.filterNot(visited.contains)
        visited ++= unvisited
        val parentStyles = unvisited.flatMap(parentName => allStyles.find(_.name == parentName))
        val grandparents = parentStyles.flatMap(_.parentStyles)
        check(grandparents)
    }

    check(style.parentStyles)
  }

  private def failureStyleRuleViolations(inheritanceRuleViolations: List[String], in: Input) = Failure(
    s"The specified styles violate the inheritance rules: '${inheritanceRuleViolations.mkString(", ")}'", in)

  private def style: Parser[StyleParseTree] = {
    name ~ opt(parentStyles) ~ leftBraces ~ description ~ attributes ~ rightBraces ^^ { parseSeq =>
      val name ~ parentStyles ~ _ ~ description ~ (attributes: List[StyleAttribute]) ~ _ = parseSeq
      StyleParseTree(
        name,
        description,
        parentStyles.getOrElse(List()),
        attributes
      )
    }
  }

  private def attributes: Parser[List[StyleAttribute]] = {
    rep(lineColor | lineStyle | lineWidth | transparency | backgroundColor | fontColor | fontName | fontSize
      | fontBold | fontItalic | gradientOrientation | gradientAreaColor | gradientAreaOffset)
      .flatMap { attributes =>
        Parser { in =>
          findAttributeDuplicates(attributes) match {
            case Nil => Success(attributes, in)
            case duplicateAttributes => failureDuplicateAttributes(duplicateAttributes, in)
          }
        }
      }
  }

  def findAttributeDuplicates(attributeList: List[StyleAttribute]): List[String] = {
    val duplicates = attributeList.groupBy(_.attributeName).collect {
      case (attributeName, attributes) if attributes.size > 1 => attributeName
    }
    duplicates.toList.sorted
  }

  private def failureDuplicateAttributes(duplicates: List[String], in: Input) = Failure(
    s"""
       |The specified style contains multiple occurrences of the following attributes (which is not allowed):"
       |'${duplicates.mkString(", ")}'
    """.stripMargin, in)

  private def name = literal("style") ~> ident

  private def description = literal("description") ~ eq ~> argument_string

  private def lineColor = literal("line-color") ~ eq ~> argument_color ^^ (arg => LineColor(arg))

  private def lineStyle = literal("line-style") ~ eq ~> argument ^^ (arg => LineStyle(arg))

  private def lineWidth = literal("line-width") ~ eq ~> argument_int ^^ (arg => LineWidth(arg))

  private def transparency = literal("transparency") ~ eq ~> argument_double ^^ (arg => Transparency(arg))

  private def backgroundColor = literal("background-color") ~ eq ~> argument_color ^^ (arg => BackgroundColor(arg))

  private def fontColor = literal("font-color") ~ eq ~> argument_color ^^ (arg => FontColor(arg))

  private def fontName = literal("font-name") ~ eq ~> argument ^^ (arg => FontName(arg))

  private def fontSize = literal("font-size") ~ eq ~> argument_int ^^ (arg => FontSize(arg))

  private def fontBold = literal("font-bold") ~ eq ~> argument_boolean ^^ (arg => FontBold(arg))

  private def fontItalic = literal("font-italic") ~ eq ~> argument_boolean ^^ (arg => FontItalic(arg))

  private def gradientOrientation = literal("gradient-orientation") ~ eq ~> argument ^^ (arg => GradientOrientation(arg))

  private def gradientAreaColor = literal("gradient-area-color") ~ eq ~> argument_color ^^ (arg => GradientAreaColor(arg))

  private def gradientAreaOffset = literal("gradient-area-offset") ~ eq ~> argument_double ^^ (arg => GradientAreaOffset(arg))

  private def parentStyles = literal("extends") ~> ident ~ rep(comma ~> ident) ^^ (parents => parents._1 :: parents._2)
}

object StyleParserImpl {

  private trait ColorToRBGColor {
    val color: Color

    val getRGBValue: String = {
      val r = color.getRed * 255.0.round.toInt
      val g = color.getGreen * 255.0.round.toInt
      val b = color.getBlue * 255.0.round.toInt

      s"$r$g$b"
    }
  }

  private case class ColorOrGradientImpl(color: Color) extends ColorOrGradient with ColorToRBGColor

  private case class ColorWithTransparencyImpl(color: Color) extends ColorWithTransparency with ColorToRBGColor

  private case class ColorImpl(color: Color) extends OldColor with ColorToRBGColor

  def convert(styleParseTree: StyleParseTree): Style = {

    class CollectAttributeWrapper[T](val t: Option[T]) {
      def map[R](func: T => R): Option[R] = t.map(func)
    }

    def collectAttribute[T: ClassTag]: CollectAttributeWrapper[T] = {
      val attribute = styleParseTree.attributes.collectFirst {
        case t: T => t
      }
      new CollectAttributeWrapper(attribute)
    }

    new Style(
      name = styleParseTree.name,
      description = Some(styleParseTree.description),
      transparency = collectAttribute[Transparency].map(_.transparency),
      background_color = collectAttribute[BackgroundColor].map(bg => ColorOrGradientImpl(bg.color)),
      line_color = collectAttribute[LineColor].map(lc => ColorWithTransparencyImpl(lc.color)),
      line_style = collectAttribute[LineStyle].map(_.style).flatMap(OldLineStyle.getIfValid),
      line_width = collectAttribute[LineWidth].map(_.width),
      font_color = collectAttribute[FontColor].map(fc => ColorImpl(fc.color)),
      font_name = collectAttribute[FontName].map(_.name),
      font_size = collectAttribute[FontSize].map(_.size),
      font_bold = collectAttribute[FontBold].map(_.bold),
      font_italic = collectAttribute[FontItalic].map(_.italic),
      gradient_orientation = collectAttribute[GradientOrientation].map(_.orientation).flatMap(GradientAlignment.ifValid),
      selected_highlighting = None,
      multiselected_highlighting = None,
      allowed_highlighting = None,
      unallowed_highlighting = None,
      parents = List()
    )
  }

}

