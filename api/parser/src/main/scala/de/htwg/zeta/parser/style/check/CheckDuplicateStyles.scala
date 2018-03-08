package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckDuplicateStyles(styleTrees: List[StyleParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findDuplicates = FindDuplicates[StyleParseTree](_.name)
    findDuplicates(styleTrees)
      .map(id => s"The following styles are defined multiple times: $id")
  }

}
