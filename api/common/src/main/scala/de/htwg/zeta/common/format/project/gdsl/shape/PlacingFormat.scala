package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class PlacingFormat(
    styleFormat: StyleFormat,
    positionFormat: PositionFormat,
    sStyle: String = "style",
    sPosition: String = "position",
    sGeoModel: String = "geoModel"
) extends OFormat[Placing] {

  override def writes(clazz: Placing): JsObject = Json.obj(
    sStyle -> styleFormat.writes(clazz.style),
    sPosition -> positionFormat.writes(clazz.position),
    sGeoModel -> ???
  )

  override def reads(json: JsValue): JsResult[Placing] = for {
    name <- (json \ sStyle).validate(styleFormat)
    position <- (json \ sPosition).validate(positionFormat)
    geoModel <- (json \ sGeoModel).validate(???)
  } yield {
    Placing(name, position, geoModel)
  }

}
