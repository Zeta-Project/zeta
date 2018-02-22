package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.{Placing, Target}
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._

sealed trait ShapeParseTree {
  val identifier: String
}

case class NodeParseTree(identifier: String,
                         conceptClass: String,
                         edges: List[String],
                         sizeMin: SizeMin,
                         sizeMax: SizeMax,
                         style: Option[NodeStyle],
                         resizing: Option[Resizing],
                         anchors: List[Anchor],
                         geoModels: List[GeoModelParseTree]) extends ShapeParseTree {

  def allGeoModels: List[GeoModelParseTree] = {
    geoModels.flatMap(allGeoModels)
  }

  private def allGeoModels(geoModelParseTree: GeoModelParseTree): List[GeoModelParseTree] = {
    geoModelParseTree +: geoModelParseTree.children.flatMap(allGeoModels)
  }
}

case class EdgeParseTree(identifier: String,
                         conceptConnection: String,
                         conceptTarget: Target,
                         placings: List[Placing]) extends ShapeParseTree


