package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.Attributes.HorizontalAlignment.HorizontalAlignment
import de.htwg.zeta.parser.shape.Attributes.VerticalAlignment.VerticalAlignment
import de.htwg.zeta.parser.shape.Attributes._

sealed trait ShapeParseTree

case class NodeParseTree(identifier: String, conceptClass: String,
                         edges: List[String], attributes: List[Attribute],
                         geoModels: List[GeoModel]) extends ShapeParseTree

sealed trait GeoModel

case class Ellipse(style: Style, position: Position, size: Size, children: List[GeoModel]) extends GeoModel
case class Textfield(identifier: Identifier, multiline: Boolean, position: Position, size: Size, align: Align) extends GeoModel

object Attributes {

  object HorizontalAlignment extends Enumeration {
    type HorizontalAlignment = Value
    val left, middle, right = Value
  }

  object VerticalAlignment extends Enumeration {
    type VerticalAlignment = Value
    val top, middle, bottom = Value
  }

  sealed trait Attribute

  case class Align(horizontal: HorizontalAlignment, vertical: VerticalAlignment)

  case class Position(x: Int, y: Int)

  case class Resizing(horizontal: Boolean, vertical: Boolean, proportional: Boolean) extends Attribute

  case class Size(width: Int, height: Int) extends Attribute

  case class SizeMax(width: Int, height: Int) extends Attribute

  case class SizeMin(width: Int, height: Int) extends Attribute

  case class Style(identifier: String) extends Attribute

  case class Identifier(name: String) extends Attribute

}


