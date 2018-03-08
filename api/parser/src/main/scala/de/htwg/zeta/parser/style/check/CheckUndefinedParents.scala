package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindUndefinedElements
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckUndefinedParents(styleTrees: List[StyleParseTree]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findUndefined = FindUndefinedElements[StyleParseTree](_.name, _.parentStyles)
    findUndefined(styleTrees)
  }

}
