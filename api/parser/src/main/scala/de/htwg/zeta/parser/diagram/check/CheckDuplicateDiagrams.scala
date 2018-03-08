package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.diagram.DiagramParseTree

case class CheckDuplicateDiagrams(diagrams: List[DiagramParseTree]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findDuplicates = FindDuplicates[DiagramParseTree](_.name)
    findDuplicates(diagrams)
  }

}
