package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.{Position, Style}
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree

object EdgeAttributes {

  sealed trait EdgeAttribute

  case class Target(target: String) extends EdgeAttribute

  case class Placing(style: Option[Style],
                     position: Position,
                     geoModel: GeoModelParseTree) extends EdgeAttribute
}
