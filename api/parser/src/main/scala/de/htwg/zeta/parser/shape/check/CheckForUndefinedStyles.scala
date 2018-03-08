package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.model.style.Style
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckForUndefinedStyles(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style]) extends ErrorCheck {

  // check if there are styles referenced which are not defined
  override def check(): List[Id] = {
    val referencedStyles = shapeParseTrees.collect {
      case node: NodeParseTree =>
        node.allGeoModels.flatMap(_.style).map(_.name) ++ node.style.map(_.name).toList
      case edge: EdgeParseTree =>
        edge.placings.map(_.geoModel).flatMap(_.style).map(_.name)
    }.flatten.toSet
    referencedStyles.diff(styles.identifiers().toSet).toList
  }

}
