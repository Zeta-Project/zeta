package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes._

sealed trait ShapeParseTree

case class NodeParseTree(identifier: String,
                         conceptClass: String,
                         edges: List[String],
                         sizeMin: SizeMin,
                         sizeMax: SizeMax,
                         style: Option[NodeStyle],
                         resizing: Option[Resizing],
                         anchors: List[Anchor],
                         geoModels: List[GeoModelParseTree]) extends ShapeParseTree


