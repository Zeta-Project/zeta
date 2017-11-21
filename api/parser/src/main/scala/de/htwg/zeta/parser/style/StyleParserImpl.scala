package de.htwg.zeta.parser.style

import de.htwg.zeta.server.generator.model.style.Style


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
    duplicates.toList.distinct.sorted
  }

  private def failureDuplicateAttributes(duplicates: List[String], in: Input) = Failure("""
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
  // private def allowed = literal("allowed") ~ eq ~> argument_color ^^ (arg => Allowed(arg))
  // private def unAllowed = literal("unallowed") ~ eq ~> argument_color ^^ (arg => UnAllowed(arg))
  // private def selected = literal("selected") ~ eq ~> argument_color ^^ (arg => Selected(arg))
  // private def multiSelected = literal("multiselected") ~ eq ~> argument_color ^^ (arg => MultiSelected(arg))
  // private def highlighting = literal("highlighting") ~ eq ~> argument ^^ (arg => Highlighting(arg))

  private def parentStyles = literal("extends") ~> ident ~ rep(comma ~> ident) ^^ (parents => parents._1 :: parents._2)

  // todo: 1. define all valid attribute keys (see above)
  // todo: 4. tests ...
  // todo: 5. function: InternalStyleModel -> StyleModel
  // todo: 6. impl. for RGB colors with #


}

