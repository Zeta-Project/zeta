package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.Anchor
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.NodeStyle
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.Resizing
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMax
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMin

sealed trait ShapeParseTree {
  val identifier: String
}

case class NodeParseTree(
    identifier: String,
    conceptClass: String,
    edges: List[String],
    sizeMin: SizeMin,
    sizeMax: SizeMax,
    style: Option[NodeStyle],
    resizing: Option[Resizing],
    anchors: List[Anchor],
    geoModels: List[GeoModelParseTree]) extends ShapeParseTree {

  // retrieve all geo models and child geo models recursive
  def allGeoModels: List[GeoModelParseTree] = {
    geoModels.flatMap(allGeoModels)
  }

  private def allGeoModels(geoModel: GeoModelParseTree): List[GeoModelParseTree] = {
    geoModel +: geoModel.children.flatMap(allGeoModels)
  }
}

case class EdgeParseTree(
    identifier: String,
    conceptConnection: String,
    conceptTarget: Target,
    placings: List[Placing]) extends ShapeParseTree


