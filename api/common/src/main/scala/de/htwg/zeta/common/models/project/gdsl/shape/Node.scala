package de.htwg.zeta.common.models.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Node(
    name: String,
    conceptElement: String, // TODO
    edges: List[Edge],
    size: Size,
    style: Style,
    resizing: Resizing,
    geoModels: List[GeoModel]
)
