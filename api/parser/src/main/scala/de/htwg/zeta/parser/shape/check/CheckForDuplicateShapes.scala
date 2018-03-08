package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckForDuplicateShapes(shapeParseTrees: List[ShapeParseTree]) extends ErrorCheck {

  // check if there are any shapes with the same identifier
  override def check(): List[Id] = {
    val findDuplicates = FindDuplicates[ShapeParseTree](_.identifier)
    findDuplicates(shapeParseTrees)
  }

}
