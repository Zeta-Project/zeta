package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.Attributes.HorizontalAlignment.HorizontalAlignment
import de.htwg.zeta.parser.shape.parsetree.Attributes.VerticalAlignment.VerticalAlignment


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

