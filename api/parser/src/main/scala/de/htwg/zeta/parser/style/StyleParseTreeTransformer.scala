package de.htwg.zeta.parser.style

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.models.project.gdsl.style
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.DoubleLine
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Solid
import de.htwg.zeta.common.models.project.gdsl.style.Style
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

    Style(
      name = styleParseTree.name,
      description = styleParseTree.description,
      background = Background(
        color = styleAttributes.?[BackgroundColor].fold(Background.defaultColor)(c => Color(c.color))
      ),
      font = Font(
        bold = styleAttributes.?[FontBold].fold(Font.defaultBold)(_.bold),
        color = styleAttributes.?[FontColor].fold(Font.defaultColor)(fc => Color(fc.color)),
        italic = styleAttributes.?[FontItalic].fold(Font.defaultItalic)(_.italic),
        name = styleAttributes.?[FontName].fold(Font.defaultName)(_.name),
        size = styleAttributes.?[FontSize].fold(Font.defaultSize)(_.size)
      ),
      line = Line(
        color = styleAttributes.?[LineColor].fold(Line.defaultColor)(lc => Color(lc.color)),
        style = styleAttributes.?[LineStyle].fold(Line.defaultStyle)(s => transformLineStyle(s.style)),
        width = styleAttributes.?[LineWidth].fold(Line.defaultWidth)(_.width)
      ),
      transparency = styleAttributes.?[Transparency].fold(Style.defaultTransparency)(_.transparency)
    )
  }

}
