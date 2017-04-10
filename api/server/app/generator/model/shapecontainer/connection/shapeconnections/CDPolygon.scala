package generator.model.shapecontainer.connection.shapeconnections

import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.style.Style

/**
 * Vorsicht CDPolygon erbt zwar von CDPolyline aber ein Polygon als Polyline zu benutzen ist nicht der eigentliche sinn
 * rein pragmatischm, da Polygon und PolyLine die selben Attribute haben
 */
class CDPolygon(
    style: Option[Style] = None,
    point1: Point,
    point2: Point,
    otherPoints: List[Point] = List[Point]())
  extends CDPolyLine(style, point1, point2, otherPoints)

