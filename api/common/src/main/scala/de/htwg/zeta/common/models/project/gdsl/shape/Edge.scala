package de.htwg.zeta.common.models.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Edge(
    name: String,
    conceptElement: String, // TODO
    target: String, // TODO
    style: Style,
    placings: List[Placing]
)
