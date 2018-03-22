package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindUndefinedElements
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckUndefinedParents(styleTrees: List[StyleParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findUndefined = FindUndefinedElements[StyleParseTree](_.name, _.parentStyles)
    findUndefined(styleTrees)
      .map(id => s"The following style is referenced as parent but not defined: $id")
  }

}
