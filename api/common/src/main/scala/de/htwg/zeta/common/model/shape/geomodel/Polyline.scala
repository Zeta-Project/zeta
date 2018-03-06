package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

class Polyline(
    points: List[Point],
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
