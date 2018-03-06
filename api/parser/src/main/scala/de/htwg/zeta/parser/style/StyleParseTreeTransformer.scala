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
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorChecker
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.check.FindGraphCycles
import de.htwg.zeta.parser.check.FindUndefinedElements

object StyleParseTreeTransformer {

  def transform(styleTrees: List[StyleParseTree]): Validation[List[String], List[Style]] = {
    checkForErrors(styleTrees) match {
      case Nil => Success(styleTrees.map(transform))
      case errors: List[String] => Failure(errors)
    }
  }

  private def checkForErrors(styleTrees: List[StyleParseTree]): List[String] = {

    def findDuplicateStyles(): List[Id] = {
      val findDuplicates = FindDuplicates[StyleParseTree](_.name)
      findDuplicates(styleTrees)
    }

    def findUndefinedParents(): List[Id] = {
      val findUndefined = FindUndefinedElements[StyleParseTree](_.name, _.parentStyles)
      findUndefined(styleTrees)
    }

    def findGraphCycles(): List[Id] = {
      val findCycles = FindGraphCycles[StyleParseTree](_.name, id => styleTrees.find(_.name == id), _.parentStyles)
      findCycles(styleTrees)
    }

    ErrorChecker()
      .add(ids => s"The following styles are defined multiple times: $ids", findDuplicateStyles)
      .add(ids => s"The following styles are referenced as parent but not defined: $ids", findUndefinedParents)
      .add(ids => s"The following styles defines a graph circle with its parent styles: $ids", findGraphCycles)
      .run()
  }

  def transform(styleParseTree: StyleParseTree): Style = {

    val styleAttributes = Collector(styleParseTree.attributes)

    def transformLineStyle(string: String): style.LineStyle = string match {
      case "dotted" => Dotted()
      case "solid" => Solid()
      case "double" => DoubleLine()
      case "dash" => Dashed()
      case _ => Line.defaultStyle
    }

    new Style(
      name = styleParseTree.name,
      description = styleParseTree.description,
      background = new Background(
        color = styleAttributes.?[BackgroundColor]
          .map(_.color)
          .map(Color(_))
          .getOrElse(Background.defaultColor)
      ),
      font = new Font(
        bold = styleAttributes.?[FontBold].map(_.bold).getOrElse(Font.defaultBold),
        color = styleAttributes.?[FontColor].map(fc => Color(fc.color))
          .getOrElse(Font.defaultColor),
        italic = styleAttributes.?[FontItalic].map(_.italic)
          .getOrElse(Font.defaultItalic),
        name = styleAttributes.?[FontName].map(_.name)
          .getOrElse(Font.defaultName),
        size = styleAttributes.?[FontSize].map(_.size)
          .getOrElse(Font.defaultSize),
        transparent = Font.defaultTransparent // TODO
      ),
      line = new Line(
        color = styleAttributes.?[LineColor].map(lc => Color(lc.color))
          .getOrElse(Line.defaultColor),
        style = styleAttributes.?[LineStyle].map(_.style).map(transformLineStyle)
          .getOrElse(Line.defaultStyle),
        transparent = Line.defaultTransparent, // TODO
        width = styleAttributes.?[LineWidth].map(_.width)
          .getOrElse(Line.defaultWidth)
      ),
      transparency = styleAttributes.?[Transparency].map(_.transparency)
        .getOrElse(Style.defaultTransparency)
    )
  }

}
