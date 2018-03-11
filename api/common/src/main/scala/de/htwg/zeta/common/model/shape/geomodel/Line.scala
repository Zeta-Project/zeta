package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class Line(
    startPoint: Point,
    endPoint: Point,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
