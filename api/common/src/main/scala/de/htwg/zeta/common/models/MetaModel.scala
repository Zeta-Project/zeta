package de.htwg.zeta.common.models

case class MetaModel(
    id: String,
    diagrams: List[Diagram],
    styles: List[Style],
    shapes: List[Shape]
)
