package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.diagram.DiagramParseTree
import de.htwg.zeta.parser.diagram.NodeParseTree

case class CheckDuplicateNodes(diagrams: List[DiagramParseTree]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findDuplicates = FindDuplicates[NodeParseTree](_.name)
    diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findDuplicates(_))
  }

}
