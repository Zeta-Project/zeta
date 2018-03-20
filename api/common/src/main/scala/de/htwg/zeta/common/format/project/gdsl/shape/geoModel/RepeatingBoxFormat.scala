package de.htwg.zeta.common.format.project.gdsl.shape.geoModel

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RepeatingBox
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class RepeatingBoxFormat(
    geoModelFormatProvider: () => GeoModelFormat,
    styleFormat: StyleFormat,
    sType: String,
    sEditable: String,
    sForEach: String,
    sForAs: String,
    sChildGeoModels: String,
    sStyle: String
) extends OFormat[RepeatingBox] {

  val vType: String = "repeatingBox"

  override def writes(clazz: RepeatingBox): JsObject = Json.obj(
    sType -> vType,
    sEditable -> clazz.editable,
    sForEach -> clazz.forEach,
    sForAs -> clazz.forAs,
    sChildGeoModels -> Writes.list(geoModelFormatProvider()).writes(clazz.childGeoModels),
    sStyle -> styleFormat.writes(clazz.style)
  )

  override def reads(json: JsValue): JsResult[RepeatingBox] = for {
    editable <- (json \ sEditable).validate[Boolean]
    forEach <- (json \ sForEach).validate[String]
    forAs <- (json \ sForAs).validate[String]
    childGeoModels <- (json \ sChildGeoModels).validate(Reads.list(geoModelFormatProvider()))
    style <- (json \ sStyle).validate(styleFormat)
  } yield {
    RepeatingBox(
      editable,
      forEach,
      forAs,
      childGeoModels,
      style
    )
  }

}
object RepeatingBoxFormat {
  def apply(geoModelFormat: () => GeoModelFormat): RepeatingBoxFormat = new RepeatingBoxFormat(
    geoModelFormat,
    StyleFormat(),
    "type",
    "editable",
    "forEach",
    "forAs",
    "childGeoElements",
    "style"
  )
}

