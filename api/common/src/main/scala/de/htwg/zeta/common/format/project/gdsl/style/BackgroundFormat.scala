package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Background
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class BackgroundFormat(
    colorFormat: ColorFormat,
    sColor: String = "color"
) extends OFormat[Background] {

  override def writes(clazz: Background): JsObject = Json.obj(
    sColor -> colorFormat.writes(clazz.color)
  )

  override def reads(json: JsValue): JsResult[Background] = for {
    color <- (json \ sColor).validate(colorFormat)
  } yield {
    Background(color)
  }

}
object BackgroundFormat {
  def apply(): BackgroundFormat = new BackgroundFormat(ColorFormat(), "color")
}
