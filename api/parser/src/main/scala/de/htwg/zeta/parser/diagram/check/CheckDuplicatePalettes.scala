package de.htwg.zeta.parser.diagram.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.diagram.DiagramParseTree
import de.htwg.zeta.parser.diagram.PaletteParseTree

case class CheckDuplicatePalettes(diagrams: List[DiagramParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findDuplicates = FindDuplicates[PaletteParseTree](_.name)
    diagrams.map(_.palettes).flatMap(findDuplicates(_))
      .map(id => s"The following palettes are defined multiple times: $id")
  }

}
