package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.HorizontalLayout
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class HorizontalLayoutFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    styleFormat: StyleFormat,
    sType: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[HorizontalLayout] {

  val vType: String = "horizontalLayout"

  override def writes(clazz: HorizontalLayout): JsObject = Json.obj(
    sType -> vType,
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[HorizontalLayout] = for {
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    HorizontalLayout(
      childGeoModels,
      style
    )
  }

}
object HorizontalLayoutFormat {
  def apply(geoModelFormat: () => GeoModelFormat): HorizontalLayoutFormat = new HorizontalLayoutFormat(
    geoModelFormat,
    StyleFormat(),
    "type",
    "childGeoElements",
    "style"
  )
}

