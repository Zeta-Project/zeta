package de.htwg.zeta.server.generator.model.shapecontainer.connection.shapeconnections

import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Point
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.LineLayout
import de.htwg.zeta.server.generator.model.style.Style

/**
 * All the possible CDElements
 */

class CDLine(
    override val style: Option[Style] = None,
    override val points: (Point, Point))
  extends ShapeConnection with LineLayout

