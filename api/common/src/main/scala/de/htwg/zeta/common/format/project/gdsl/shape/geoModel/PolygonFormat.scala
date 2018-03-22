package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class PolygonFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    pointFormat: PointFormat,
    styleFormat: StyleFormat,
    sType: String,
    sPoints: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[Polygon] {

  val vType: String = "polygon"

  override def writes(clazz: Polygon): JsObject = Json.obj(
    sType -> vType,
    sPoints -> Writes.list(pointFormat).writes(clazz.points),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Polygon] = for {
    points <- (json \ sPoints).validate(Reads.list(pointFormat))
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Polygon(
      points,
      childGeoModels,
      style
    )
  }

}
object PolygonFormat {
  def apply(geoModelFormat: () => GeoModelFormat): PolygonFormat = new PolygonFormat(
    geoModelFormat,
    PointFormat(),
    StyleFormat(),
    "type",
    "points",
    "childGeoElements",
    "style"
  )
}

