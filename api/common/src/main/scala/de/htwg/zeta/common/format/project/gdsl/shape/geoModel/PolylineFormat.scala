package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class PolylineFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    pointFormat: PointFormat,
    styleFormat: StyleFormat,
    sType: String,
    sPoints: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[Polyline] {

  val vType: String = "polyline"

  override def writes(clazz: Polyline): JsObject = Json.obj(
    sType -> vType,
    sPoints -> Writes.list(pointFormat).writes(clazz.points),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Polyline] = for {
    points <- (json \ sPoints).validate(Reads.list(pointFormat))
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Polyline(
      points,
      childGeoModels,
      style
    )
  }

}
object PolylineFormat {
  def apply(geoModelFormat: () => GeoModelFormat): PolylineFormat = new PolylineFormat(
    geoModelFormat,
    PointFormat(),
    StyleFormat(),
    "type",
    "points",
    "childGeoElements",
    "style"
  )
}

