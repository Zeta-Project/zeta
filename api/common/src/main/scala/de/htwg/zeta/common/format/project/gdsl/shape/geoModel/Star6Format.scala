package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Star6
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class Star6Format(
                   geoModelFormatProvider: () => GeoModelFormat,
                   sizeFormat: SizeFormat,
                   positionFormat: PositionFormat,
                   styleFormat: StyleFormat,
                   sType: String,
                   sSize: String,
                   sPosition: String,
                   sChildGeoModels: String,
                   sStyle: String
                 ) extends OFormat[Star6] {

  val vType: String = "star6"

  override def writes(clazz: Star6): JsObject = Json.obj(
    sType -> vType,
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Star6] = for {
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Star6(
      size,
      position,
      childGeoModels,
      style
    )
  }

}

object Star6Format {
  def apply(geoModelFormat: () => GeoModelFormat): Star6Format = new Star6Format(
    geoModelFormat,
    SizeFormat(),
    PositionFormat(),
    StyleFormat(),
    "type",
    "size",
    "position",
    "childGeoElements",
    "style"
  )
}
