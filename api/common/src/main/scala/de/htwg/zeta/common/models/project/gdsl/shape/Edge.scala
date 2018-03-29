package de.htwg.zeta.common.models.project.gdsl.shape

case class Edge(
    name: String,
    conceptElement: String, // TODO
    target: String, // TODO
    placings: List[Placing]
)
