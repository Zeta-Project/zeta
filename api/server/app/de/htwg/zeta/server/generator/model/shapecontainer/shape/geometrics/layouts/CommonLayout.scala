package de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts

import scala.annotation.tailrec

import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.GeoModel
import de.htwg.zeta.server.generator.parser.CommonParserMethods
import de.htwg.zeta.server.generator.parser.IDtoStyle
import grizzled.slf4j.Logging


/**
 * Created by julian on 15.10.15.
 * the commonLayout (from the Grammarsheet)
 */
trait CommonLayout extends Layout {
  val position: Option[(Int, Int)] // (x,y) Tuple
  val size_width: Int
  val size_height: Int

  // unsafe getter!
  lazy val (x, y) = position.getOrElse(0, 0)

}

/**
 * CommonLayoutParser
 */
object CommonLayoutParser extends CommonParserMethods with Logging {

  private case class Mapping(
      posOpt: Option[(Int, Int)] = None,
      sizeOpt: Option[(Int, Int)] = None,
      styleOpt: Option[Style] = None)

  private def parseRek(geoModel: GeoModel, parentStyle: Option[Style], cache: Cache): Mapping = {
    // if geoModel.style and parentstyle are defined a childStyle is created
    val defaultStyle: Option[Style] = Style.generateChildStyle(cache, parentStyle, geoModel.style)


    @tailrec
    def rek(attributes: List[String], mappings: Mapping): Mapping = {
      (attributes, mappings) match {
        case (Nil, _) | (_, Mapping(Some(_), Some(_), Some(_))) =>
          mappings

        case (head :: tail, Mapping(None, _, _)) if head.matches("position.+") =>
          val posOpt = parse(position, head).get
          rek(tail, mappings.copy(posOpt = posOpt))

        case (head :: tail, Mapping(_, None, _)) if head.matches("size.+") =>
          val sizeOpt = parse(size, head).get
          rek(tail, mappings.copy(sizeOpt = sizeOpt))

        case (head :: tail, Mapping(_, _, None)) if cache.styleHierarchy.contains(head) =>
          // generate anonymous style)
          val styleOpt = Style.generateChildStyle(cache, defaultStyle, IDtoStyle(head)(cache))
          rek(tail, mappings.copy(styleOpt = styleOpt))

        case (_ :: tail, _) =>
          rek(tail, mappings)
      }
    }

    val mappings = rek(geoModel.attributes, Mapping())
    mappings.copy(styleOpt = mappings.styleOpt.orElse(defaultStyle))
  }


  /**
   * @param geoModel    GeoModel instance
   * @param parentStyle Style instance
   * @param cache       Cache instance
   * @return CommonLayout instance
   */
  def parse(geoModel: GeoModel, parentStyle: Option[Style], cache: Cache): Option[CommonLayout] = {

    val mappings = parseRek(geoModel, parentStyle, cache)

    mappings.sizeOpt match {
      case Some((width, height)) =>
        Some(CommonLayoutDefaultImpl(mappings.posOpt, width, height, mappings.styleOpt))
      case None =>
        info(s"no size was given for Position in: ${geoModel.typ}")
        None
    }
  }
}

private case class CommonLayoutDefaultImpl(
    override val position: Option[(Int, Int)],
    override val size_width: Int,
    override val size_height: Int,
    override val style: Option[Style]
) extends CommonLayout
