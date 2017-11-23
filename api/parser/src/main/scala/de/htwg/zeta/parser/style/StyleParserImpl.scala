package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.model.style.{LineStyle => OldLineStyle}
import de.htwg.zeta.server.generator.model.style.color.{Color => OldColor}
import de.htwg.zeta.server.generator.model.style.color.ColorOrGradient
import de.htwg.zeta.server.generator.model.style.color.ColorWithTransparency
import de.htwg.zeta.server.generator.model.style.gradient.GradientAlignment
import javafx.scene.paint.Color


class StyleParserImpl extends StyleParser {

  private val leftBraces = literal("{")
  private val rightBraces = literal("}")
  private val eq = literal("=")
  private val comma = literal(",")

  override def style: Parser[StyleParseModel] = {
    name ~ opt(parentStyles) ~ leftBraces ~ description ~ attributes ~ rightBraces ^^ { parseSeq =>
      val name ~ parentStyles ~ _ ~ description ~ (attributes: List[StyleAttribute]) ~ _ = parseSeq
      StyleParseModel(
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
          findDuplicates(attributes) match {
            case Nil => Success(attributes, in)
            case duplicateAttributes => failureDuplicateAttributes(duplicateAttributes, in)
          }
        }
      }
  }

  def findDuplicates(attributeList: List[StyleAttribute]): List[String] = {
    val duplicates = attributeList.groupBy(_.attributeName).collect {
      case (attributeName, attributes) if attributes.size > 1 => attributeName
    }
    duplicates.toList.sorted
  }

  private def failureDuplicateAttributes(duplicates: List[String], in: Input) = Failure(
    """
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

  def convert(styleParseModel: StyleParseModel): Style = {

    class CollectAttributeWrapper[T](val t: Option[T]) {
      def apply[R](func: T => R): Option[R] = t.map(func)
    }

    def collectAttribute[T]: CollectAttributeWrapper[T] = {
      new CollectAttributeWrapper(
        styleParseModel.attributes.collectFirst {
          case t: T => t
        })
    }

    new Style(
      name = styleParseModel.name,
      description = Some(styleParseModel.description),
      transparency = collectAttribute[Transparency](_.transparency),
      background_color = collectAttribute[BackgroundColor](bg => ColorOrGradientImpl(bg.color)),
      line_color = collectAttribute[LineColor](lc => ColorWithTransparencyImpl(lc.color)),
      line_style = collectAttribute[LineStyle](_.style).flatMap(OldLineStyle.getIfValid),
      line_width = collectAttribute[LineWidth](_.width),
      font_color = collectAttribute[FontColor](fc => ColorImpl(fc.color)),
      font_name = collectAttribute[FontName](_.name),
      font_size = collectAttribute[FontSize](_.size),
      font_bold = collectAttribute[FontBold](_.bold),
      font_italic = collectAttribute[FontItalic](_.italic),
      gradient_orientation = collectAttribute[GradientOrientation](_.orientation).flatMap(GradientAlignment.ifValid),
      selected_highlighting = None,
      multiselected_highlighting = None,
      allowed_highlighting = None,
      unallowed_highlighting = None,
      parents = List()
    )
  }

}

