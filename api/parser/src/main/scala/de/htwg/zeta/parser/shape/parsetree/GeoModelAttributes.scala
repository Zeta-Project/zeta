package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.HorizontalAlignment.HorizontalAlignment
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.VerticalAlignment.VerticalAlignment


object GeoModelAttributes {

  object HorizontalAlignment extends Enumeration {
    type HorizontalAlignment = Value
    val left, middle, right = Value
  }

  object VerticalAlignment extends Enumeration {
    type VerticalAlignment = Value
    val top, middle, bottom = Value
  }

  sealed trait GeoModelAttribute

  case class Align(horizontal: HorizontalAlignment, vertical: VerticalAlignment) extends GeoModelAttribute

  case class Curve(width: Int, height: Int) extends GeoModelAttribute

  case class Position(x: Int, y: Int) extends GeoModelAttribute

  case class Point(x: Int, y: Int) extends GeoModelAttribute

  case class Size(width: Int, height: Int) extends GeoModelAttribute

  case class Style(name: String) extends GeoModelAttribute

  case class Identifier(name: String) extends GeoModelAttribute {
    def split: (String, String) = {
      name.split("\\.").toList match {
        case prefix :: identifier :: Nil => (prefix, identifier)
        case _ => ("", name)
      }
    }
  }

  case class Multiline(multiline: Boolean) extends GeoModelAttribute

  case class For(each: Identifier, as: String) extends GeoModelAttribute

  case class Editable(editable: Boolean) extends GeoModelAttribute

  case class Text(text: String) extends GeoModelAttribute

  case class TextBody(text: String) extends GeoModelAttribute

}

