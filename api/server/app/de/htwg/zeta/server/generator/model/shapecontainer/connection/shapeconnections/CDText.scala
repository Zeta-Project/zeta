package de.htwg.zeta.server.generator.model.shapecontainer.connection.shapeconnections

import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Alignment.VAlign
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Alignment.HAlign
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.TextLayout
import de.htwg.zeta.server.generator.model.style.Style

/**
 * Created by julian on 20.10.15.
 */
class CDText(
    override val textBody: String,
    override val style: Option[Style] = None,
    override val hAlign: Option[HAlign],
    override val vAlign: Option[VAlign],
    override val position: Option[(Int, Int)] = None,
    override val size_width: Int,
    override val size_height: Int)
  extends ShapeConnection with TextLayout

