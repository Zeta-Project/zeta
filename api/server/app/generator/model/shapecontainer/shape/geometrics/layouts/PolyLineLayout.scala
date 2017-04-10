package generator.model.shapecontainer.shape.geometrics.layouts

import generator.model.shapecontainer.shape.geometrics.PointParser
import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.GeoModel
import parser._

/**
 * Created by julian on 20.10.15.
 * representation of a polylinelayout
 */
trait PolyLineLayout extends Layout {
  val points: List[Point]
}

object PolyLineLayoutParser {
  def apply(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[PolyLineLayout] = {
    parse(geoModel, parentStyle, hierarchyContainer)
  }
  def parse(geoModel: GeoModel, parentStyle: Option[Style], hierarchyContainer: Cache): Option[PolyLineLayout] = {
    implicit val cache = hierarchyContainer
    val attributes = geoModel.attributes

    /*mapping*/
    var collectedPoints: List[Point] = List[Point]()
    var styl: Option[Style] = Style.generateChildStyle(hierarchyContainer, parentStyle, geoModel.style)
    attributes.foreach {
      case x if x.matches("point.+") =>
        val newPoint = PointParser(x)
        if (newPoint.isDefined) collectedPoints = collectedPoints.::(newPoint.get)
      case anonymousStyle: String if hierarchyContainer.styleHierarchy.contains(anonymousStyle) =>
        styl = Style.generateChildStyle(hierarchyContainer, styl, Some(anonymousStyle))
      case _ =>
    }
    if (collectedPoints.length > 1)
      Some(new PolyLineLayout {
        override val points: List[Point] = collectedPoints
        override val style: Option[Style] = styl
      })
    else
      None
  }
}
