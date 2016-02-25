package generator.model.shapecontainer.shape.geometrics

import generator.model.shapecontainer.shape.geometrics.layouts.{LineLayoutParser, LineLayout}
import generator.model.style.Style
import generator.parser.{Cache, GeoModel}

/**
 * Created by julian on 15.10.15.
 * Representation of a simple Line
 */
sealed class Line private (parent:Option[GeometricModel]=None,
           override val style:Option[Style] = None,
           override val points:(Point, Point))
  extends GeometricModel(parent) with LineLayout {
  def x1 = points._1.x
  def y1 = points._1.y
  def x2 = points._2.x
  def y2 = points._2.y
}

object Line{
  def apply(geoModel: GeoModel, parent:Option[GeometricModel], parentStyle:Option[Style], hierarchyContainer: Cache) = parse(geoModel, parent, parentStyle, hierarchyContainer)
  def parse(geoModel: GeoModel, parent:Option[GeometricModel], parentStyle:Option[Style], hierarchyContainer: Cache): Option[Line] = {
    val lineLayout = LineLayoutParser.parse(geoModel, parentStyle, hierarchyContainer)
    if(lineLayout.isEmpty)
      None
    else
      Some(new Line(parent, lineLayout.get.style, lineLayout.get.points))
  }
}
