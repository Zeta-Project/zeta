package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class StaticText(
    text: String,
    size: Size,
    position: Position,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
