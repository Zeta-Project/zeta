package de.htwg.zeta.common.model.shape.geomodel

import de.htwg.zeta.common.model.style.Style

case class TextField(
    identifier: String,
    size: Size,
    position: Position,
    editable: Boolean,
    multiline: Boolean,
    align: Align,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel
