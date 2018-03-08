package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.common.model.shape.Node
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindInvalidReferences
import de.htwg.zeta.parser.diagram.DiagramParseTree
import de.htwg.zeta.parser.diagram.NodeParseTree

case class CheckUndefinedNodes(diagrams: List[DiagramParseTree], nodes: ReferenceCollector[Node]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findInvalidIds = FindInvalidReferences[NodeParseTree](_.name, nodes.identifiers())
    diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findInvalidIds(_))
  }

}
