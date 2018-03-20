package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Font
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class FontFormat(
    colorFormat: ColorFormat,
    sName: String = "name",
    sBold: String = "bold",
    sColor: String = "color",
    sItalic: String = "italic",
    sSize: String = "size"
) extends OFormat[Font] {

  override def writes(clazz: Font): JsObject = Json.obj(
    sName -> clazz.name,
    sBold -> clazz.bold,
    sColor -> colorFormat.writes(clazz.color),
    sItalic -> clazz.italic,
    sSize -> clazz.size
  )

  override def reads(json: JsValue): JsResult[Font] = for {
    name <- (json \ sName).validate[String]
    bold <- (json \ sBold).validate[Boolean]
    color <- (json \ sColor).validate(colorFormat)
    italic <- (json \ sItalic).validate[Boolean]
    size <- (json \ sSize).validate[Int]
  } yield {
    Font(name, bold, color, italic, size)
  }

}
object FontFormat {
  def apply(): FontFormat = new FontFormat(
    ColorFormat(),
    "name", "bold", "color", "italic", "size"
  )
}
