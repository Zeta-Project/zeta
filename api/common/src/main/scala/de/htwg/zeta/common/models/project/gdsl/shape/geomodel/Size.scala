package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

case class Size(
    width: Int,
    height: Int
)

object Size {
  val default: Size = Size(1, 1)
}
