package de.htwg.zeta.common.model.shape.geomodel

case class Position(
    x: Int,
    y: Int
)

object Position {
  val default: Position = Position(0, 0)
}
