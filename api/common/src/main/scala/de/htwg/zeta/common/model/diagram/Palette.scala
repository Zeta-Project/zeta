package de.htwg.zeta.common.model.diagram

import de.htwg.zeta.common.model.shape.Node

case class Palette(
    name: String,
    nodes: List[Node]
)
