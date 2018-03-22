package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.StaticText
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class StaticTextFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    sizeFormat: SizeFormat,
    positionFormat: PositionFormat,
    styleFormat: StyleFormat,
    sType: String,
    sSize: String,
    sPosition: String,
    sText: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[StaticText] {

  val vType: String = "statictext"

  override def writes(clazz: StaticText): JsObject = Json.obj(
    sType -> vType,
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sText -> clazz.text,
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[StaticText] = for {
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
    text <- (json \ sText).validate[String]
  } yield {
    StaticText(
      text,
      size,
      position,
      childGeoModels,
      style
    )
  }

}
object StaticTextFormat {
  def apply(geoModelFormat: () => GeoModelFormat): StaticTextFormat = new StaticTextFormat(
    geoModelFormat,
    SizeFormat(),
    PositionFormat(),
    StyleFormat(),
    "type",
    "size",
    "position",
    "text",
    "childGeoElements",
    "style"
  )
}

