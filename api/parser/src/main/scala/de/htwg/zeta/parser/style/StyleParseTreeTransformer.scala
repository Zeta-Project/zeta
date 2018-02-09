package de.htwg.zeta.parser.style

import javafx.scene.paint.Color

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.{FindDuplicates, FindGraphCycles, FindUndefinedParents}
import de.htwg.zeta.server.generator.model.style.color.{ColorOrGradient, ColorWithTransparency, Color => OldColor}
import de.htwg.zeta.server.generator.model.style.gradient.GradientAlignment
import de.htwg.zeta.server.generator.model.style.{Style, LineStyle => OldLineStyle}

import scala.reflect.ClassTag
import scalaz.{Failure, Success, Validation}

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
    val findUndefinedParents = new FindUndefinedParents[StyleParseTree](toId, getParentIds)
    val findGraphCycles = new FindGraphCycles[StyleParseTree](toId, toElement, getParentIds)

    val checks = List(findDuplicates, findUndefinedParents, findGraphCycles)
    checks.flatMap(check => check(styleTrees))
  }

  def transform(styleParseTree: StyleParseTree): Style = {

    trait ColorToRBGColor {
      val color: Color

      val getRGBValue: String = {
        val r = color.getRed * 255.0.round.toInt
        val g = color.getGreen * 255.0.round.toInt
        val b = color.getBlue * 255.0.round.toInt

        s"$r$g$b"
      }
    }

    case class ColorOrGradientImpl(color: Color) extends ColorOrGradient with ColorToRBGColor

    case class ColorWithTransparencyImpl(color: Color) extends ColorWithTransparency with ColorToRBGColor

    case class ColorImpl(color: Color) extends OldColor with ColorToRBGColor

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
