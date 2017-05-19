package de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics

import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.RoundedRectangleLayoutParser
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.RoundedRectangleLayout
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.GeoModel

/**
 * Created by julian on 19.10.15.
 * represents a rounded rectangle
 */
sealed class RoundedRectangle private (
    parent: Option[GeometricModel] = None,
    rrLayout: RoundedRectangleLayout,
    wrapping: List[GeoModel]
) extends GeometricModel(parent) with RoundedRectangleLayout with Wrapper {
  override val style: Option[Style] = rrLayout.style
  override val curve_width: Int = rrLayout.curve_width // from RoundedRectangleLayout
  override val curve_height: Int = rrLayout.curve_height // from RoundedRectangleLayout
  override val position: Option[(Int, Int)] = rrLayout.position
  override val size_width: Int = rrLayout.size_width
  override val size_height: Int = rrLayout.size_height
  override val children: List[GeometricModel] = wrapping.map(_.parse(Some(this), style).get)

}

object RoundedRectangle {
  /**
   * parses a GeoModel into an actual GeometricModel, in this case a Rectangle
   * @param geoModel is the sketch to parse into a GeometricModel
   * @param parent is the parent instance that wraps the new GeometricModel
   * @param parentStyle is the style used by the parent and eventual will be merged with the geoModels style to a new style
   * @param hierarchyContainer holds hierarchical information about styles and is therefor needed
   */
  def apply(geoModel: GeoModel, parent: Option[GeometricModel], parentStyle: Option[Style], hierarchyContainer: Cache) = {
    parse(geoModel, parent, parentStyle, hierarchyContainer)
  }

  private def parse(
    geoModel: GeoModel,
    parent: Option[GeometricModel],
    parentStyle: Option[Style],
    hierarchyContainer: Cache): Option[RoundedRectangle] = {

    // mapping
    val rrLayout: Option[RoundedRectangleLayout] = RoundedRectangleLayoutParser.parse(geoModel, parentStyle, hierarchyContainer)

    if (rrLayout.isEmpty) {
      None
    } else {
      Some(new RoundedRectangle(parent, rrLayout.get, geoModel.children))
    }
  }
}
