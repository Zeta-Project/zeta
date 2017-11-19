package de.htwg.zeta.parser.style


class StyleParserImpl extends StyleParser {

  private val leftBraces  = literal("{")
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
    rep(lineColor | lineStyle | lineWidth).flatMap { attributes =>
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

  /*
  val validStyleAttributes = List("description", "transparency", "background-color", "line-color", "line-style", "line-width",
    "font-color", "font-name", "font-size", "font-bold", "font-italic", "gradient-orientation", "gradient-area-color",
    "gradient-area-offset", "allowed", "unallowed", "selected", "multiselected", "highlighting")
   */
  private def name = literal("style") ~> ident
  private def description = literal("description") ~ eq ~> argument_string
  private def lineColor = literal("line-color") ~ eq ~> argument ^^ (arg => LineColor(arg))
  private def lineStyle = literal("line-style") ~ eq ~> argument ^^ (arg => LineStyle(arg))
  private def lineWidth = literal("line-width") ~ eq ~> argument_int ^^ (arg => LineWidth(arg))

  private def parentStyles = literal("extends") ~> ident ~ rep(comma ~> ident) ^^ (parents => parents._1 :: parents._2)

  // todo: 1. define all valid attribute keys (see above)
  // todo: 4. tests ...
  // todo: 5. function: InternalStyleModel -> StyleModel
  private def gradientOrientation = literal("gradient-orientation") ~ eq ~> argument
  private def backgroundColor = literal("background-color") ~ eq ~> argument
  private def fontSize = literal("font-size") ~ eq ~> argument_int



}

