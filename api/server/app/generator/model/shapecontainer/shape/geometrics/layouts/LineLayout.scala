package generator.model.shapecontainer.shape.geometrics.layouts

import generator.model.shapecontainer.shape.geometrics.{ PointParser, Point }
import generator.model.style.Style
import generator.parser.{ Cache, GeoModel }
import parser._

/**
 * Created by julian on 20.10.15.
 * representation of a LineLayout
 */
trait LineLayout extends Layout {
  val points: (Point, Point)
}

object LineLayoutParser {
  def parse(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[LineLayout] = {
    implicit val cache = hierarchyContainer
    val attributes = geoModel.attributes

    /*mapping*/
    var point1: Option[Point] = None
    var point2: Option[Point] = None
    var styl: Option[Style] = Style.generateChildStyle(hierarchyContainer, parentStyle, geoModel.style)
    attributes.foreach {
      case x if x.matches("point.+") =>
        if (point1.isEmpty)
          point1 = PointParser(x)
        else {
          point2 = PointParser(x)
        }
      case anonymousStyle: String if hierarchyContainer.styleHierarchy.contains(anonymousStyle) =>
        styl = Style.generateChildStyle(hierarchyContainer, styl, Some(anonymousStyle))
      case _ =>
    }
    if (point1.isDefined && point2.isDefined)
      Some(new LineLayout {
        override val style = styl
        override val points: (Point, Point) = (point1.get, point2.get)
      })
    else
      None
  }
}
