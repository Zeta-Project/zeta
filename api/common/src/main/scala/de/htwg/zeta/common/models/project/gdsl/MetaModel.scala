package de.htwg.zeta.common.models.project.gdsl

import de.htwg.zeta.common.models.project.gdsl.diagram.Diagram
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.common.models.project.gdsl.style.Style

case class MetaModel(
    id: String,
    diagrams: List[Diagram],
    styles: List[Style],
    shape: Shape
)
