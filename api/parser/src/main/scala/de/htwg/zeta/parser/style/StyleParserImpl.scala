package de.htwg.zeta.parser.style

class StyleParserImpl extends StyleParser {

  private val leftBraces  = literal("{")
  private val rightBraces = literal("}")
  private val eq = literal("=")

  override def style: Parser[StyleParseModel] = {
    // parentstyle wird string-liste (kann optional sein)
    name ~ leftBraces ~ description ~ attributes ~ rightBraces ^^ { parseSeq =>
      val name ~ _ ~ description ~ (attributes: List[StyleAttribute]) ~ _ = parseSeq
      StyleParseModel(
        name,
        description,
        List(), /* parentStyles, */
        attributes
      )
    }
  }

  private def attributes: Parser[List[StyleAttribute]] = {
    rep(lineColor | lineStyle | lineWidth).flatMap { attributes =>
      Parser { in =>
        findDuplicates(attributes) match {
          case Nil => Success(attributes, in)
          case duplicateFields => Failure(duplicateFields.toString, in)
        }
      }
    }
  }

  // todo: 2. check for duplicate attribute keys (not allowed). Update Failure Msg
  def findDuplicates(attributes: List[StyleAttribute]): List[String] = List()

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

  // todo: 0. rename
  // todo: 1. define all valid attribute keys (see above)
  // todo: 3. parse parent styles
  // todo: 4. tests ...
  // todo: 5. function: InternalStyleModel -> StyleModel
  private def gradientOrientation = literal("gradient-orientation") ~ eq ~> argument
  private def backgroundColor = literal("background-color") ~ eq ~> argument
  private def fontSize = literal("font-size") ~ eq ~> argument_int

}

