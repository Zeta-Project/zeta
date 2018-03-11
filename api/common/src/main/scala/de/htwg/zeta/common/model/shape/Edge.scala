package de.htwg.zeta.common.model.shape

case class Edge(
    name: String,
    conceptElement: String, // TODO
    target: String, // TODO
    placings: List[Placing]
)
