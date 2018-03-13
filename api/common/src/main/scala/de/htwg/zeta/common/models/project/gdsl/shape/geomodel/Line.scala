package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Line(
    startPoint: Point,
    endPoint: Point,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
