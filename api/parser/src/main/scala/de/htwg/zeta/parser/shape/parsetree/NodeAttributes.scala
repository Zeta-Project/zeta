package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.AnchorPosition.AnchorPosition

object NodeAttributes {

  trait NodeAttribute

  case class NodeStyle(name: String) extends NodeAttribute

  case class Resizing(horizontal: Boolean, vertical: Boolean, proportional: Boolean) extends NodeAttribute

  case class SizeMax(width: Int, height: Int) extends NodeAttribute

  case class SizeMin(width: Int, height: Int) extends NodeAttribute

  abstract class Anchor extends NodeAttribute

  case class RelativeAnchor(xOffset: Int, yOffset: Int) extends Anchor

  case class AbsoluteAnchor(x: Int, y: Int) extends Anchor

  case class PredefinedAnchor(anchorPosition: AnchorPosition) extends Anchor

  object AnchorPosition extends Enumeration {
    type AnchorPosition = Value
    val corner, center, edges = Value
  }

}
