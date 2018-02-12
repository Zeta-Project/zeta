package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.NodeAttributes.NodeAttribute

sealed trait ShapeParseTree

case class NodeParseTree(identifier: String, conceptClass: String,
                         edges: List[String], attributes: List[NodeAttribute],
                         geoModels: List[GeoModel]) extends ShapeParseTree

sealed trait GeoModel

object NodeAttributes {

  sealed trait NodeAttribute

  case class Style(identifier: String) extends NodeAttribute

  case class SizeMin(width: Int, height: Int) extends NodeAttribute

  case class SizeMax(width: Int, height: Int) extends NodeAttribute

  case class Resizing(horizontal: Boolean, vertical: Boolean, proportional: Boolean) extends NodeAttribute

}




