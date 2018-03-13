package de.htwg.zeta.common.models.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Placing(
    style: Style,
    position: Position,
    geoModel: GeoModel
)
