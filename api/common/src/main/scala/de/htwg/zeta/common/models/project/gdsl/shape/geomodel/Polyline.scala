package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Polyline(
    points: List[Point],
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
