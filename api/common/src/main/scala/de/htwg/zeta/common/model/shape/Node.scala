package de.htwg.zeta.common.model.shape

import de.htwg.zeta.common.model.shape.geomodel.GeoModel
import de.htwg.zeta.common.model.style.Style

case class Node(
    name: String,
    conceptElement: String, // TODO
    edges: List[Edge],
    size: Size,
    style: Style,
    resizing: Resizing,
    geoModels: List[GeoModel]
)
