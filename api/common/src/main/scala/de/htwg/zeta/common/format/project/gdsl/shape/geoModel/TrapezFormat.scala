package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Trapez
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class TrapezFormat(
                    geoModelFormatProvider: () => GeoModelFormat,
                    sizeFormat: SizeFormat,
                    positionFormat: PositionFormat,
                    styleFormat: StyleFormat,
                    sType: String,
                    sSize: String,
                    sPosition: String,
                    sChildGeoModels: String,
                    sStyle: String
                  ) extends OFormat[Trapez] {

  val vType: String = "trapez"

  override def writes(clazz: Trapez): JsObject = Json.obj(
    sType -> vType,
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Trapez] = for {
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Trapez(
      size,
      position,
      childGeoModels,
      style
    )
  }

}

object TrapezFormat {
  def apply(geoModelFormat: () => GeoModelFormat): TrapezFormat = new TrapezFormat(
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
