package de.htwg.zeta.common.format.project.gdsl.style

import de.htwg.zeta.common.models.project.gdsl.style.Color
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ColorFormat(
    sR: String = "r",
    sG: String = "g",
    sB: String = "b"
) extends OFormat[Color] {

  override def writes(clazz: Color): JsObject = Json.obj(
    sR -> clazz.r,
    sG -> clazz.g,
    sB -> clazz.b
  )

  override def reads(json: JsValue): JsResult[Color] = for {
    r <- (json \ sR).validate[Int]
    g <- (json \ sG).validate[Int]
    b <- (json \ sB).validate[Int]
  } yield {
    Color(r, g, b)
  }

}
