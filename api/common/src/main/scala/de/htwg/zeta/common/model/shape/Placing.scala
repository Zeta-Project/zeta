package de.htwg.zeta.common.model.shape

import de.htwg.zeta.common.model.shape.geomodel.GeoModel
import de.htwg.zeta.common.model.style.Style

case class Placing(
    style: Style,
    position: Position,
    geoModel: GeoModel
)
