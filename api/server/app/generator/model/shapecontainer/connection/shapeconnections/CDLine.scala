package generator.model.shapecontainer.connection.shapeconnections

import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.shapecontainer.shape.geometrics.layouts.LineLayout
import generator.model.style.Style

/**
 * All the possible CDElements
 */

class CDLine(
    override val style: Option[Style] = None,
    override val points: (Point, Point))
  extends ShapeConnection with LineLayout

