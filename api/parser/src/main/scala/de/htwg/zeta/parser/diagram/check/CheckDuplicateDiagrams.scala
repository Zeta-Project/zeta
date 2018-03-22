package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.diagram.DiagramParseTree

case class CheckDuplicateDiagrams(diagrams: List[DiagramParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findDuplicates = FindDuplicates[DiagramParseTree](_.name)
    findDuplicates(diagrams)
      .map(id => s"The following diagram is defined multiple times: $id")
  }

}
