package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class ResizingFormat(
    sHorizontal: String,
    sVertical: String,
    sProportional: String
) extends OFormat[Resizing] {

  override def writes(clazz: Resizing): JsObject = Json.obj(
    sHorizontal -> clazz.horizontal,
    sVertical -> clazz.vertical,
    sProportional -> clazz.proportional
  )

  override def reads(json: JsValue): JsResult[Resizing] = for {
    horizontal <- (json \ sHorizontal).validate[Boolean]
    vertical <- (json \ sVertical).validate[Boolean]
    proportional <- (json \ sProportional).validate[Boolean]
  } yield {
    Resizing(
      horizontal = horizontal,
      vertical = vertical,
      proportional = proportional
    )
  }

}
object ResizingFormat {
  def apply(): ResizingFormat = new ResizingFormat(
    "horizontal",
    "vertical",
    "proportional"
  )
}
