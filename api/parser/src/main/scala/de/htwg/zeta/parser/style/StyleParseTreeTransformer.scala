package de.htwg.zeta.parser.style

import scala.annotation.tailrec
import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.models.project.gdsl.style
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
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
      case Nil =>
        // first order all styles to transform all parent styles before
        // their children to pass them to children transform method
        val styles = orderStyleTreesByParents(styleTrees)
          .foldLeft(List[Style]())((styles, parseTree) => styles :+ transformStyle(styles, parseTree))
        // for correct handling of a default style in frontend, we have to append
        // the default style always to the list of all styles
        Success(Style.defaultStyle :: styles)
      case errors: List[String] => Failure(errors)
    }
  }

  private def checkForErrors(styleTrees: List[StyleParseTree]): List[String] =
    ErrorChecker()
      .add(CheckDuplicateStyles(styleTrees))
      .add(CheckUndefinedParents(styleTrees))
      .add(CheckGraphCycles(styleTrees))
      .run()

  private def orderStyleTreesByParents(unordered: List[StyleParseTree]): List[StyleParseTree] = {
    @tailrec
    def orderStyleTrees(children: List[StyleParseTree], definedParents: List[StyleParseTree]): List[StyleParseTree] = {
      val definedParentNames = definedParents.map(_.name)
      val definedChildren = children.filter(p => p.parentStyles.count(s => !definedParentNames.contains(s)) == 0)
      val notDefinedChildren = children.filter(p => p.parentStyles.count(s => !definedParentNames.contains(s)) != 0)
      if (notDefinedChildren.isEmpty) {
        definedParents ::: definedChildren
      } else {
        orderStyleTrees(notDefinedChildren, definedParents ::: definedChildren)
      }
    }

    val withoutParents = unordered.filter(f => f.parentStyles.isEmpty)
    val withParents = unordered.filter(f => f.parentStyles.nonEmpty)
    orderStyleTrees(withParents, withoutParents)
  }

  private def transformStyle(possibleParentStyles: List[Style], styleParseTree: StyleParseTree): Style = {

    def parentOrDefault[T](default: T, styleValue: Style => T): T = {
      val parentStyles = possibleParentStyles
        .filter(p => styleParseTree.parentStyles.contains(p.name))

      if (parentStyles.isEmpty) {
        default
      } else {
        // first map all parent styles to input list, to achieve the correct
        // order of parent styles (important for correct overwrite in next step)
        val mappedParents = parentStyles.map(_.name).zip(parentStyles).toMap
        // then find first parent value which is different from the default value
        styleParseTree.parentStyles
          .map(mappedParents)
          .map(styleValue)
          .find(_ != default)
          .getOrElse(default)
      }
    }

    val attrs = Collector(styleParseTree.attributes)

    Style(
      name = styleParseTree.name,
      description = styleParseTree.description,
      background = Background(
        color = attrs.?[BackgroundColor].fold(parentOrDefault(Background.defaultColor, _.background.color))(c => Color(c.color))
      ),
      font = Font(
        bold = attrs.?[FontBold].fold(parentOrDefault(Font.defaultBold, _.font.bold))(_.bold),
        color = attrs.?[FontColor].fold(parentOrDefault(Font.defaultColor, _.font.color))(fc => Color(fc.color)),
        italic = attrs.?[FontItalic].fold(parentOrDefault(Font.defaultItalic, _.font.italic))(_.italic),
        name = attrs.?[FontName].fold(parentOrDefault(Font.defaultName, _.font.name))(_.name),
        size = attrs.?[FontSize].fold(parentOrDefault(Font.defaultSize, _.font.size))(_.size)
      ),
      line = Line(
        color = attrs.?[LineColor].fold(parentOrDefault(Line.defaultColor, _.line.color))(lc => Color(lc.color)),
        style = attrs.?[LineStyle].fold(parentOrDefault(Line.defaultStyle, _.line.style))(s => transformLineStyle(s.style)),
        width = attrs.?[LineWidth].fold(parentOrDefault(Line.defaultWidth, _.line.width))(_.width)
      ),
      transparency = attrs.?[Transparency].fold(parentOrDefault(Style.defaultTransparency, _.transparency))(_.transparency)
    )
  }

  private def transformLineStyle(string: String): style.LineStyle = string match {
    case "dotted" => Dotted()
    case "solid" => Solid()
    case "dash" => Dashed()
    case _ => Line.defaultStyle
  }

}
