package de.htwg.zeta.common.models.project.gdsl

import de.htwg.zeta.common.model.diagram.Diagram
import de.htwg.zeta.common.model.shape.Shape
import de.htwg.zeta.common.model.style.Style

case class MetaModel(
    id: String,
    diagrams: List[Diagram],
    styles: List[Style],
    shape: Shape
)
