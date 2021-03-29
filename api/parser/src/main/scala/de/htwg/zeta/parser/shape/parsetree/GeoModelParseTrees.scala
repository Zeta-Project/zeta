package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Align
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Curve
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Editable
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.For
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Multiline
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Point
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Text
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.TextBody


object GeoModelParseTrees {

  trait GeoModelParseTree {
    val style: Option[Style]
    val children: List[GeoModelParseTree]
  }

  trait HasIdentifier {
    val identifier: Identifier
  }

  case class EllipseParseTree(
      style: Option[Style],
      position: Position,
      size: Size,
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class TriangleParseTree(
                               style: Option[Style],
                               position: Position,
                               size: Size,
                               children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class HexagonParseTree(
                                style: Option[Style],
                                position: Position,
                                size: Size,
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class OctagonParseTree(
                                style: Option[Style],
                                position: Position,
                                size: Size,
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class DiamondParseTree(
                                style: Option[Style],
                                position: Position,
                                size: Size,
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class Star8ParseTree(
                                style: Option[Style],
                                position: Position,
                                size: Size,
                                children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class Star5ParseTree(
                             style: Option[Style],
                             position: Position,
                             size: Size,
                             children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class Star6ParseTree(
                             style: Option[Style],
                             position: Position,
                             size: Size,
                             children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class TrapezParseTree(
                             style: Option[Style],
                             position: Position,
                             size: Size,
                             children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class ShearedRectangleParseTree(
                             style: Option[Style],
                             position: Position,
                             size: Size,
                             children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class TextfieldParseTree(
      style: Option[Style],
      identifier: Identifier,
      textBody: Option[TextBody],
      position: Position,
      size: Size,
      multiline: Option[Multiline],
      align: Option[Align],
      editable: Option[Editable],
      children: List[GeoModelParseTree]) extends GeoModelParseTree with HasIdentifier

  case class StatictextParseTree(
      style: Option[Style],
      size: Size,
      position: Position,
      text: Text,
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class RepeatingBoxParseTree(
      editable: Editable,
      foreach: For,
      children: List[GeoModelParseTree]) extends GeoModelParseTree {
    override val style: Option[Style] = None
  }

  case class LineParseTree(
      style: Option[Style],
      startPoint: Point,
      endPoint: Point,
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class PolylineParseTree(
      style: Option[Style],
      points: List[Point],
      children: List[GeoModelParseTree]) extends GeoModelParseTree {
    require(points.size >= 2)
  }

  case class PolygonParseTree(
      style: Option[Style],
      points: List[Point],
      children: List[GeoModelParseTree]) extends GeoModelParseTree {
    require(points.size >= 2)
  }

  case class RectangleParseTree(
      style: Option[Style],
      position: Position,
      size: Size,
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class RoundedRectangleParseTree(
      style: Option[Style],
      position: Position,
      size: Size,
      curve: Curve,
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class HorizontalLayoutParseTree(
      style: Option[Style],
      children: List[GeoModelParseTree]) extends GeoModelParseTree

  case class VerticalLayoutParseTree(
      style: Option[Style],
      children: List[GeoModelParseTree]) extends GeoModelParseTree

}