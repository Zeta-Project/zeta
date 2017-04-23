package generator.model.shapecontainer.shape.geometrics

import generator.model.shapecontainer.shape.geometrics.layouts.PolyLineLayoutParser
import generator.model.shapecontainer.shape.geometrics.layouts.PolyLineLayout
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.GeoModel

/**
 * Created by julian on 19.10.15.
 * represents a Polyline - several lines, definded by deveral Points.
 * the least amount of points is 2, the standardconstructor requires point1 and point2
 * several other points can be added in a list, or by varargs
 */
class PolyLine(
    parent: Option[GeometricModel] = None,
    polyLineLayout: PolyLineLayout
) extends GeometricModel(parent) with PolyLineLayout {
  override val style: Option[Style] = polyLineLayout.style
  override val points: List[Point] = polyLineLayout.points
}

/**
 * PolyLine
 */
object PolyLine {

  /**
   * @param geoModel GeoModel instance
   * @param parent GeometricModel instance
   * @param parentStyle Style instance
   * @param hierarchyContainer Cache instance
   * @return PolyLine instance
   */
  def apply(geoModel: GeoModel, parent: Option[GeometricModel], parentStyle: Option[Style], hierarchyContainer: Cache): Option[PolyLine] = {
    parse(geoModel, parent, parentStyle, hierarchyContainer)
  }

  private def parse(
    geoModel: GeoModel,
    parent: Option[GeometricModel],
    parentStyle: Option[Style],
    hierarchyContainer: Cache): Option[PolyLine] = {

    val polyLineLayout: Option[PolyLineLayout] = PolyLineLayoutParser(geoModel, parentStyle, hierarchyContainer)
    if (polyLineLayout.isEmpty) {
      None
    } else {
      Some(new PolyLine(parent, polyLineLayout.get))
    }
  }
}
