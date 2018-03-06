package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class RepeatingBox(
    editable: Boolean,
    forEach: String, // TODO?
    forAs: String, // TODO?
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
