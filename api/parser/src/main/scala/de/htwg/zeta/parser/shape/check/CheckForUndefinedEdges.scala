package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckForUndefinedEdges(shapeParseTrees: List[ShapeParseTree]) extends ErrorCheck {

  // check if there are nodes which reference an edge which is not defined
  override def check(): List[Id] = {
    val definedEdges = shapeParseTrees.collect {
      case edge: EdgeParseTree => edge.identifier
    }.toSet
    val referencedEdges = shapeParseTrees.collect {
      case node: NodeParseTree => node.edges
    }.flatten.toSet
    referencedEdges.diff(definedEdges).toList
  }

}
