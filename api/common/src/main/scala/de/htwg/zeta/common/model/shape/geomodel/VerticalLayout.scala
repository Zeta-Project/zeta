package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class VerticalLayout(
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
