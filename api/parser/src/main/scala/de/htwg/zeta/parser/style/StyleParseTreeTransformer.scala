package de.htwg.zeta.parser.style

import scala.reflect.ClassTag
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
import de.htwg.zeta.parser.check.Check.Id
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
    val toId: StyleParseTree => Id = _.name
    val getParentIds: StyleParseTree => List[Id] = _.parentStyles
    val toElement: Id => Option[StyleParseTree] = id => styleTrees.find(_.name == id)

    val findDuplicates = new FindDuplicates[StyleParseTree](toId)
    val findUndefinedParents = new FindUndefinedElements[StyleParseTree](toId, getParentIds)
    val findGraphCycles = new FindGraphCycles[StyleParseTree](toId, toElement, getParentIds)

    val checks = List(findDuplicates, findUndefinedParents, findGraphCycles)
    checks.flatMap(check => check(styleTrees))
  }

  def transform(styleParseTree: StyleParseTree): Style = {

    class CollectAttributeWrapper[T](val t: Option[T]) {
      def map[R](func: T => R): Option[R] = t.map(func)
    }

    def collectAttribute[T: ClassTag]: CollectAttributeWrapper[T] = {
      val attribute = styleParseTree.attributes.collectFirst {
        case t: T => t
      }
      new CollectAttributeWrapper(attribute)
    }

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
        color = collectAttribute[BackgroundColor]
          .map(bg => Color(bg.color))
          .getOrElse(Background.defaultColor)
      ),
      font = new Font(
        bold = collectAttribute[FontBold].map(_.bold).getOrElse(Font.defaultBold),
        color = collectAttribute[FontColor].map(fc => Color(fc.color))
          .getOrElse(Font.defaultColor),
        italic = collectAttribute[FontItalic].map(_.italic)
          .getOrElse(Font.defaultItalic),
        name = collectAttribute[FontName].map(_.name)
          .getOrElse(Font.defaultName),
        size = collectAttribute[FontSize].map(_.size)
          .getOrElse(Font.defaultSize),
        transparent = Font.defaultTransparent // TODO
      ),
      line = new Line(
        color = collectAttribute[LineColor].map(lc => Color(lc.color))
          .getOrElse(Line.defaultColor),
        style = collectAttribute[LineStyle].map(_.style).map(transformLineStyle)
          .getOrElse(Line.defaultStyle),
        transparent = Line.defaultTransparent, // TODO
        width = collectAttribute[LineWidth].map(_.width)
          .getOrElse(Line.defaultWidth)
      ),
      transparency = collectAttribute[Transparency].map(_.transparency)
        .getOrElse(Style.defaultTransparency)
    )
  }

}
