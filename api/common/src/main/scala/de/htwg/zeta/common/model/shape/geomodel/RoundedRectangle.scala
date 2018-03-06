package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class RoundedRectangle(
    size: Size,
    curve: Size,
    position: Position,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
