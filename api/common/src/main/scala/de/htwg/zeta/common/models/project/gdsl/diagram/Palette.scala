package de.htwg.zeta.common.models.project.gdsl.diagram

import de.htwg.zeta.common.models.project.gdsl.shape.Node

case class Palette(
    name: String,
    nodes: List[Node]
)
