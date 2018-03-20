package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Style
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

class StyleFormat(
    backgroundFormat: BackgroundFormat,
    fontFormat: FontFormat,
    lineFormat: LineFormat,
    sName: String,
    sDescription: String,
    sBackground: String,
    sFont: String,
    sLine: String,
    sTransparency: String
) extends OFormat[Style] {

  override def writes(clazz: Style): JsObject = Json.obj(
    sName -> clazz.name,
    sDescription -> clazz.description,
    sBackground -> backgroundFormat.writes(clazz.background),
    sFont -> fontFormat.writes(clazz.font),
    sLine -> lineFormat.writes(clazz.line),
    sTransparency -> clazz.transparency
  )

  override def reads(json: JsValue): JsResult[Style] = for {
    name <- (json \ sName).validate[String]
    description <- (json \ sDescription).validate[String]
    background <- (json \ sBackground).validate(backgroundFormat)
    font <- (json \ sFont).validate(fontFormat)
    line <- (json \ sLine).validate(lineFormat)
    transparency <- (json \ sTransparency).validate[Double]
  } yield {
    Style(
      name,
      description,
      background,
      font,
      line,
      transparency
    )
  }

}
object StyleFormat {
  def apply(): StyleFormat = new StyleFormat(
    BackgroundFormat(), FontFormat(), LineFormat(),
    "name", "description", "background", "font", "line", "transparency"
  )
}
