package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class RoundedRectangleFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    sizeFormat: SizeFormat,
    positionFormat: PositionFormat,
    styleFormat: StyleFormat,
    curveFormat: SizeFormat,
    sType: String,
    sCurve: String,
    sSize: String,
    sPosition: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[RoundedRectangle] {

  val vType: String = "roundedRectangle"

  override def writes(clazz: RoundedRectangle): JsObject = Json.obj(
    sType -> vType,
    sCurve -> curveFormat.writes(clazz.curve),
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[RoundedRectangle] = for {
    curve <- (json \ sCurve).validate(curveFormat)
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    RoundedRectangle(
      curve,
      size,
      position,
      childGeoModels,
      style
    )
  }

}
object RoundedRectangleFormat {
  def apply(geoModelFormat: () => GeoModelFormat): RoundedRectangleFormat = new RoundedRectangleFormat(
    geoModelFormat,
    SizeFormat(),
    PositionFormat(),
    StyleFormat(),
    SizeFormat(),
    "type",
    "curve",
    "size",
    "position",
    "childGeoElements",
    "style"
  )
}

