package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

import de.htwg.zeta.common.models.project.gdsl.style.Style

case class TextField(
    identifier: String,
    textBody: String,
    size: Size,
    position: Position,
    editable: Boolean,
    multiline: Boolean,
    align: Align,
    childGeoModels: List[GeoModel],
    style: Style
) extends GeoModel

object TextField {
  val default: TextField = TextField(
    identifier = "default",
    textBody = "",
    size = Size.default,
    position = Position.default,
    editable = false,
    multiline = false,
    align = Align.default,
    childGeoModels = List(),
    style = Style.defaultStyle
  )
}
