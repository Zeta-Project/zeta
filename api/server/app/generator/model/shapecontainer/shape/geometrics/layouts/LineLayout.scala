package generator.model.shapecontainer.shape.geometrics.layouts

import generator.model.shapecontainer.shape.geometrics.PointParser
import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.GeoModel
import generator.parser.IDtoStyle

/**
 * Created by julian on 20.10.15.
 * representation of a LineLayout
 */
trait LineLayout extends Layout {
  val points: (Point, Point)
}

case class LineLayoutDefaultImpl(
    override val style: Option[Style],
    override val points: (Point, Point)
) extends LineLayout

/**
 * LineLayoutParser
 */
object LineLayoutParser {

  /**
   * @param geoModel           GeoModel instance
   * @param parentStyle        Style instance
   * @param hierarchyContainer Cache instance
   * @return LineLayout instance
   */
  def parse(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[LineLayout] = {
    val attributes = geoModel.attributes

    val stream = attributes.toStream.filter(_.matches("point.+")).flatMap(PointParser(_))
    val point1Opt = stream.headOption
    val point2Opt = stream.drop(1).headOption

    val defaultStyle: Option[Style] = Style.generateChildStyle(hierarchyContainer, parentStyle, geoModel.style)
    val style: Option[Style] = attributes.find(hierarchyContainer.styleHierarchy.contains)
      .flatMap(x => Style.generateChildStyle(hierarchyContainer, defaultStyle, Some(IDtoStyle(x)(hierarchyContainer))))
      .orElse(defaultStyle)

    (point1Opt,point2Opt) match {
      case (Some(point1), Some(point2)) =>
        Some(LineLayoutDefaultImpl(style, (point1, point2)))
      case _ => None
    }
  }
}
