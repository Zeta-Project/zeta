package de.htwg.zeta.common.format.project.gdsl

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.style.Style
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class StylesFormat(
    styleFormat: StyleFormat,
    sStyles: String = "styles"
) extends OFormat[List[Style]] {

  override def writes(clazz: List[Style]): JsObject = Json.obj(
    sStyles -> Writes.list(styleFormat).writes(clazz)
  )

  override def reads(json: JsValue): JsResult[List[Style]] =
    (json \ sStyles).validate(Reads.list(styleFormat))

}
