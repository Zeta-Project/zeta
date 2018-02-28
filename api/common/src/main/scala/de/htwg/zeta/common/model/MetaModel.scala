package de.htwg.zeta.common.model

import de.htwg.zeta.common.model.diagram.Diagram
import de.htwg.zeta.common.model.shape.Shape
import de.htwg.zeta.common.model.style.Style

case class MetaModel(
    id: String,
    diagrams: List[Diagram],
    styles: List[Style],
    shapes: List[Shape]
)
