package de.htwg.zeta.server.generator.model.shapecontainer.connection.shapeconnections

import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Point
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.PolyLineLayout
import de.htwg.zeta.server.generator.model.style.Style

/**
 * Created by julian on 20.10.15.
 */
class CDPolyLine(
    override val style: Option[Style] = None,
    point1: Point,
    point2: Point,
    otherPoints: List[Point] = List[Point]()
) extends ShapeConnection with PolyLineLayout {
  var l: List[Point] = List(point1, point2)
  override val points: List[Point] = List(point1, point2) ::: otherPoints
}
