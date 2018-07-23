package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindGraphCycles
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckGraphCycles(styleTrees: List[StyleParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findCycles = FindGraphCycles[StyleParseTree](_.name, id => styleTrees.find(_.name == id), _.parentStyles)
    findCycles(styleTrees) match {
      case Nil => Nil
      case cycles: List[Id] => List(s"The following styles define a cyclic graph with its parent styles: ${cycles.mkString(", ")}")
    }
  }

}
