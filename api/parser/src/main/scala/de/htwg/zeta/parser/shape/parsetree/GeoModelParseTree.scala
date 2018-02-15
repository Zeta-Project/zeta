package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.Attributes._

abstract class GeoModelParseTree(val style: Option[Style])

trait Children {
  val children: List[GeoModelParseTree]
}

case class EllipseParseTree(override val style: Option[Style],
                            position: Position,
                            size: Size,
                            children: List[GeoModelParseTree]) extends GeoModelParseTree(style) with Children

case class TextfieldParseTree(override val style: Option[Style],
                              identifier: Identifier,
                              multiline: Multiline,
                              position: Position,
                              size: Size,
                              align: Align) extends GeoModelParseTree(style)

case class RepeatingBoxParseTree(editable: Editable,
                                 foreach: For,
                                 children: List[GeoModelParseTree]) extends GeoModelParseTree(None) with Children

case class LineParseTree(override val style: Option[Style],
                         from: Point,
                         to: Point) extends GeoModelParseTree(style)

case class PolylineParseTree(override val style: Option[Style],
                             points: List[Point]) extends GeoModelParseTree(style) {
  require(points.size >= 2)
}

case class PolygonParseTree(override val style: Option[Style],
                            curvedPoints: List[CurvedPoint]) extends GeoModelParseTree(style) {
  require(curvedPoints.size >= 2)
}

case class RectangleParseTree(override val style: Option[Style],
                              position: Position,
                              size: Size,
                              curve: Option[Curve],
                              children: List[GeoModelParseTree]) extends GeoModelParseTree(style) with Children