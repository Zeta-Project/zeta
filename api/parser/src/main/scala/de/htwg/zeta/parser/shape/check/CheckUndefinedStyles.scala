package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.project.gdsl.style.Style
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckUndefinedStyles(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style]) extends ErrorCheck[ErrorMessage] {

  // check if there are styles referenced which are not defined
  override def check(): List[ErrorMessage] = {
    val referencedStyles = shapeParseTrees.collect {
      case node: NodeParseTree =>
        node.allGeoModels.flatMap(_.style).map(_.name) ++ node.style.map(_.name).toList
      case edge: EdgeParseTree =>
        edge.placings.map(_.geoModel).flatMap(_.style).map(_.name)
    }.flatten.toSet
    referencedStyles.diff(styles.identifiers().toSet).toList match {
      case Nil => Nil
      case undefinedStyles: List[Id] => List(s"The following styles are referenced but not defined: ${undefinedStyles.mkString(", ")}")
    }
  }

}
