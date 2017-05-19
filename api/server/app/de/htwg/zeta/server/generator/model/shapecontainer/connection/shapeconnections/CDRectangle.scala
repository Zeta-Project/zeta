package de.htwg.zeta.server.generator.model.shapecontainer.connection.shapeconnections

import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.RectangleEllipseLayout
import de.htwg.zeta.server.generator.model.style.Style

/**
 * Created by julian on 20.10.15.
 */
class CDRectangle(
    override val style: Option[Style] = None,
    override val position: Option[(Int, Int)] = None,
    override val size_width: Int,
    override val size_height: Int)
  extends ShapeConnection with RectangleEllipseLayout

