package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.Attributes.Attribute

sealed trait ShapeParseTree

case class NodeParseTree(identifier: String,
                         conceptClass: String,
                         edges: List[String],
                         attributes: List[Attribute],
                         geoModels: List[GeoModelParseTree]) extends ShapeParseTree


