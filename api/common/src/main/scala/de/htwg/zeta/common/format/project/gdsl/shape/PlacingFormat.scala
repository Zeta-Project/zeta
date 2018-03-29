package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.format.project.gdsl.shape.geoModel.GeoModelFormat
import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class PlacingFormat(
    geoModelFormat: GeoModelFormat,
    styleFormat: StyleFormat,
    positionFormat: PositionFormat,
    sStyle: String,
    sPosition: String,
    sGeoModel: String
) extends OFormat[Placing] {

  override def writes(clazz: Placing): JsObject = Json.obj(
    sStyle -> styleFormat.writes(clazz.style),
    sPosition -> positionFormat.writes(clazz.position),
    sGeoModel -> geoModelFormat.writes(clazz.geoModel)
  )

  override def reads(json: JsValue): JsResult[Placing] = for {
    name <- (json \ sStyle).validate(styleFormat)
    position <- (json \ sPosition).validate(positionFormat)
    geoModel <- (json \ sGeoModel).validate(geoModelFormat)
  } yield {
    Placing(name, position, geoModel)
  }

}
object PlacingFormat {
  def apply(): PlacingFormat = new PlacingFormat(
    GeoModelFormat(),
    StyleFormat(),
    PositionFormat(),
    "style", "position", "geoElement"
  )
}


