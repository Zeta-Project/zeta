package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckDuplicateShapes(shapeParseTrees: List[ShapeParseTree]) extends ErrorCheck[ErrorMessage] {

  // check if there are any shapes with the same identifier
  override def check(): List[ErrorMessage] = {
    val findDuplicates = FindDuplicates[ShapeParseTree](_.identifier)
    findDuplicates(shapeParseTrees)
      .map(id => s"The following shapes are defined multiple times: $id")
  }

}
