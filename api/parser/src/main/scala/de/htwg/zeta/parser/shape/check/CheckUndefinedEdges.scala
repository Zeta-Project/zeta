package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckUndefinedEdges(shapeParseTrees: List[ShapeParseTree]) extends ErrorCheck[ErrorMessage] {

  // check if there are nodes which reference an edge which is not defined
  override def check(): List[ErrorMessage] = {
    val definedEdges = shapeParseTrees.collect {
      case edge: EdgeParseTree => edge.identifier
    }.toSet
    val referencedEdges = shapeParseTrees.collect {
      case node: NodeParseTree => node.edges
    }.flatten.toSet
    referencedEdges.diff(definedEdges)
      .map(id => s"The following edges are referenced but not defined: $id").toList
  }

}
