package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindGraphCycles
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckGraphCycles(styleTrees: List[StyleParseTree]) extends ErrorCheck {

  override def check(): List[Id] = {
    val findCycles = FindGraphCycles[StyleParseTree](_.name, id => styleTrees.find(_.name == id), _.parentStyles)
    findCycles(styleTrees)
  }

}
