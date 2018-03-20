package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class EllipseFormat(
    geoModelFormat: () => GeoModelFormat,
    sizeFormat: SizeFormat,
    positionFormat: PositionFormat,
    styleFormat: StyleFormat,
    sType: String = "type",
    sSize: String = "size",
    sPosition: String = "position",
    sChildGeoModels: String = "childGeoModels",
    sStyle: String = "style"
) extends OFormat[Ellipse] {

  val vType: String = "ellipse"

  override def writes(clazz: Ellipse): JsObject = Json.obj(
    sType -> vType,
    sSize -> sizeFormat.writes(clazz.size),
    sPosition -> positionFormat.writes(clazz.position),
    sChildGeoModels -> Writes.list(geoModelFormat()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[Ellipse] = for {
    size <- (json \ sSize).validate(sizeFormat)
    position <- (json \ sPosition).validate(positionFormat)
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormat()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    Ellipse(
      size,
      position,
      childGeoModels,
      style
    )
  }

}
object EllipseFormat {
  def apply(styleFormat: StyleFormat, geoModelFormat: () => GeoModelFormat): EllipseFormat = new EllipseFormat(
    geoModelFormat,
    SizeFormat(),
    PositionFormat(),
    styleFormat,
    "type",
    "size",
    "position",
    "childGeoModels",
    "style"
  )
}
