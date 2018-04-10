package de.htwg.zeta.parser.shape.parsetree

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree

object EdgeAttributes {

  sealed trait EdgeAttribute

  case class Target(target: String) extends EdgeAttribute

  case class Placing(
      style: Option[Style],
      offset: Offset,
      geoModel: GeoModelParseTree
  ) extends EdgeAttribute

  case class Offset(offset: Double) extends EdgeAttribute

}
