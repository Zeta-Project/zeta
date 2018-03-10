package de.htwg.zeta.parser.style

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.model.style
import de.htwg.zeta.common.model.style.Background
import de.htwg.zeta.common.model.style.Color
import de.htwg.zeta.common.model.style.Dashed
import de.htwg.zeta.common.model.style.Dotted
import de.htwg.zeta.common.model.style.DoubleLine
import de.htwg.zeta.common.model.style.Font
import de.htwg.zeta.common.model.style.Line
import de.htwg.zeta.common.model.style.Solid
import de.htwg.zeta.common.model.style.Style
import de.htwg.zeta.parser.Collector
import de.htwg.zeta.parser.check.ErrorChecker
import de.htwg.zeta.parser.style.check.CheckDuplicateStyles
import de.htwg.zeta.parser.style.check.CheckGraphCycles
import de.htwg.zeta.parser.style.check.CheckUndefinedParents

object StyleParseTreeTransformer {

  def transform(styleTrees: List[StyleParseTree]): Validation[List[String], List[Style]] = {
    checkForErrors(styleTrees) match {
      case Nil => Success(styleTrees.map(transformStyle))
      case errors: List[String] => Failure(errors)
    }
  }

  private def checkForErrors(styleTrees: List[StyleParseTree]): List[String] =
    ErrorChecker()
      .add(CheckDuplicateStyles(styleTrees))
      .add(CheckUndefinedParents(styleTrees))
      .add(CheckGraphCycles(styleTrees))
      .run()

  private def transformStyle(styleParseTree: StyleParseTree): Style = {
    def transformLineStyle(string: String): style.LineStyle = string match {
      case "dotted" => Dotted()
      case "solid" => Solid()
      case "double" => DoubleLine()
      case "dash" => Dashed()
      case _ => Line.defaultStyle
    }

    val styleAttributes = Collector(styleParseTree.attributes)

    new Style(
      name = styleParseTree.name,
      description = styleParseTree.description,
      background = new Background(
        color = styleAttributes.?[BackgroundColor].map(c => Color(c.color)).getOrElse(Background.defaultColor)
      ),
      font = new Font(
        bold = styleAttributes.?[FontBold].map(_.bold).getOrElse(Font.defaultBold),
        color = styleAttributes.?[FontColor].map(fc => Color(fc.color)).getOrElse(Font.defaultColor),
        italic = styleAttributes.?[FontItalic].map(_.italic).getOrElse(Font.defaultItalic),
        name = styleAttributes.?[FontName].map(_.name).getOrElse(Font.defaultName),
        size = styleAttributes.?[FontSize].map(_.size).getOrElse(Font.defaultSize)
      ),
      line = new Line(
        color = styleAttributes.?[LineColor].map(lc => Color(lc.color)).getOrElse(Line.defaultColor),
        style = styleAttributes.?[LineStyle].map(_.style).map(transformLineStyle).getOrElse(Line.defaultStyle),
        width = styleAttributes.?[LineWidth].map(_.width).getOrElse(Line.defaultWidth)
      ),
      transparency = styleAttributes.?[Transparency].map(_.transparency).getOrElse(Style.defaultTransparency)
    )
  }

}
