package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._


object GeoModelParseTrees {

  trait GeoModelParseTree {
    val style: Option[Style]
    val children: List[GeoModelParseTree]
  }

  case class EllipseParseTree(style: Option[Style],
                              position: Position,
                              size: Size,
                              children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class TextfieldParseTree(style: Option[Style],
                                identifier: Identifier,
                                position: Position,
                                size: Size,
                                multiline: Option[Multiline],
                                align: Option[Align],
                                editable: Option[Editable],
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class StatictextParseTree(style: Option[Style],
                                 size: Size,
                                 position: Position,
                                 text: Text,
                                 children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class RepeatingBoxParseTree(editable: Editable,
                                   foreach: For,
                                   children: List[GeoModelParseTree]) extends GeoModelParseTree {
    override val style: Option[Style] = None
  }

  case class LineParseTree(style: Option[Style],
                           startPoint: Point,
                           endPoint: Point,
                           children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class PolylineParseTree(style: Option[Style],
                               points: List[Point],
                               children: List[GeoModelParseTree]) extends GeoModelParseTree {
    require(points.size >= 2)
  }

  case class PolygonParseTree(style: Option[Style],
                              points: List[Point],
                              children: List[GeoModelParseTree]) extends GeoModelParseTree {
    require(points.size >= 2)
  }

  case class RectangleParseTree(style: Option[Style],
                                position: Position,
                                size: Size,
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class RoundedRectangleParseTree(style: Option[Style],
                                       position: Position,
                                       size: Size,
                                       curve: Curve,
                                       children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class HorizontalLayoutParseTree(style: Option[Style],
                                       children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class VerticalLayoutParseTree(style: Option[Style],
                                     children: List[GeoModelParseTree]) extends GeoModelParseTree

}