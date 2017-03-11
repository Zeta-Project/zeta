package generator.model.shapecontainer.connection.shapeconnections

import generator.model.style.Style

/**
 * Vorsicht Ellipse erbt zwar von Rectangle, aber eine Ellipse als ein Rectangle zu benutzen ist nicht der eigentliche Sinn
 * rein pragmatisch, da Ellipse und Rectangle die selben Attribute haben
 */
class CDEllipse(
  style: Option[Style] = None,
  position: Option[(Int, Int)] = None,
  size_width: Int,
  size_height: Int
) extends CDRectangle(style, position, size_width, size_height)
