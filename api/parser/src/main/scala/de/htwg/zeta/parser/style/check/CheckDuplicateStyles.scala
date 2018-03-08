package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckDuplicateStyles(styleTrees: List[StyleParseTree]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findDuplicates = FindDuplicates[StyleParseTree](_.name)
    findDuplicates(styleTrees)
  }

}
