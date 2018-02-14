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

case class Textfield(identifier: Identifier, multiline: Multiline, position: Position, size: Size, align: Align) extends GeoModel

case class RepeatingBox(editable: Editable, foreach: For, children: List[GeoModel]) extends GeoModel

case class Line(style: Option[Style], from: Point, to: Point) extends GeoModel

case class Polyline(style: Option[Style], points: List[Point]) extends GeoModel {
  require(points.size >= 2)
}

case class Polygon(style: Option[Style], curvedPoints: List[CurvedPoint]) extends GeoModel {
  require(curvedPoints.size >= 2)
}

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

  case class Align(horizontal: HorizontalAlignment, vertical: VerticalAlignment) extends Attribute

  case class Position(x: Int, y: Int) extends Attribute

  case class Point(x: Int, y: Int) extends Attribute

  case class CurvedPoint(x: Int, y: Int, curveBefore: Int, curveAfter: Int) extends Attribute

  case class Resizing(horizontal: Boolean, vertical: Boolean, proportional: Boolean) extends Attribute

  case class Size(width: Int, height: Int) extends Attribute

  case class SizeMax(width: Int, height: Int) extends Attribute

  case class SizeMin(width: Int, height: Int) extends Attribute

  case class Style(identifier: String) extends Attribute

  case class Identifier(name: String) extends Attribute

  case class Multiline(multiline: Boolean) extends Attribute

  case class For(each: String, as: String) extends Attribute

  case class Editable(editable: Boolean) extends Attribute

}


