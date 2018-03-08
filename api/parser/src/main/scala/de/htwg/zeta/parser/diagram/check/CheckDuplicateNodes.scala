package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.diagram.DiagramParseTree
import de.htwg.zeta.parser.diagram.NodeParseTree

case class CheckDuplicateNodes(diagrams: List[DiagramParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findDuplicates = FindDuplicates[NodeParseTree](_.name)
    diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findDuplicates(_))
      .map(id => s"The following node is defined multiple times: $id")
  }

}
