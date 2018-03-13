package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindInvalidReferences
import de.htwg.zeta.parser.diagram.DiagramParseTree
import de.htwg.zeta.parser.diagram.NodeParseTree

case class CheckUndefinedNodes(diagrams: List[DiagramParseTree], nodes: ReferenceCollector[Node]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findInvalidIds = FindInvalidReferences[NodeParseTree](_.name, nodes.identifiers())
    diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findInvalidIds(_))
      .map(id => s"The following nodes are not defined in shape: $id")
  }

}
