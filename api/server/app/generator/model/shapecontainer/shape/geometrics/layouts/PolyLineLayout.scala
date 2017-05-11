package generator.model.shapecontainer.shape.geometrics.layouts

import generator.model.shapecontainer.shape.geometrics.PointParser
import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.GeoModel
import generator.parser.IDtoStyle

/**
 * Created by julian on 20.10.15.
 * representation of a polylinelayout
 */
trait PolyLineLayout extends Layout {
  val points: List[Point]
}

case class PolyLineLayoutDefaultImpl(
    override val points: List[Point],
    override val style: Option[Style]) extends PolyLineLayout

/**
 * PolyLineLayoutParser
 */
object PolyLineLayoutParser {

  /**
   * @param geoModel           GeoModel instance
   * @param parentStyle        Style instance
   * @param hierarchyContainer Cache instance
   * @return PolyLineLayout instance
   */
  def apply(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[PolyLineLayout] = {
    parse(geoModel, parentStyle, hierarchyContainer)
  }

  private def parse(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[PolyLineLayout] = {
    val attributes = geoModel.attributes

    val collectedPoints = attributes.filter(_.matches("point.+")).flatMap(PointParser(_))
    val defaultStyle: Option[Style] = Style.generateChildStyle(hierarchyContainer, parentStyle, geoModel.style)
    val style: Option[Style] = attributes.find(hierarchyContainer.styleHierarchy.contains)
      .flatMap(x => Style.generateChildStyle(hierarchyContainer, defaultStyle, Some(IDtoStyle(x)(hierarchyContainer))))
      .orElse(defaultStyle)

    collectedPoints match {
      case Nil => None
      case _ => Some(PolyLineLayoutDefaultImpl(collectedPoints,style))
    }
  }
}
