package de.htwg.zeta.parser.style.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindGraphCycles
import de.htwg.zeta.parser.style.StyleParseTree

case class CheckGraphCycles(styleTrees: List[StyleParseTree]) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val findCycles = FindGraphCycles[StyleParseTree](_.name, id => styleTrees.find(_.name == id), _.parentStyles)
    val ids = findCycles(styleTrees)
    if (ids.isEmpty) {
      List()
    } else {
      val idList = ids.mkString(", ")
      List(s"The following styles defines a graph circle with its parent styles: $idList")
    }
  }

}
