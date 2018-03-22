package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Line
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class LineFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    pointFormat: PointFormat,
    styleFormat: StyleFormat,
    sType: String,
    sStartPoint: String,
    sEndPoint: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[Line] {

  val vType: String = "line"

  override def writes(clazz: Line): JsObject = Json.obj(
    sType -> vType,
    sStartPoint -> pointFormat.writes(clazz.startPoint),
    sEndPoint -> pointFormat.writes(clazz.endPoint),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Line] = for {
    startPoint <- (json \ sStartPoint).validate(pointFormat)
    endPoint <- (json \ sEndPoint).validate(pointFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Line(
      startPoint,
      endPoint,
      childGeoModels,
      style
    )
  }

}
object LineFormat {
  def apply(geoModelFormat: () => GeoModelFormat): LineFormat = new LineFormat(
    geoModelFormat,
    PointFormat(),
    StyleFormat(),
    "type",
    "startPoint",
    "endPoint",
    "childGeoElements",
    "style"
  )
}

