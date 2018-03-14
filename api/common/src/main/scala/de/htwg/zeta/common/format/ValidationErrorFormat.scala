package de.htwg.zeta.common.format

import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class ValidationErrorFormat(
    sMessages: String = "messages"
) extends OFormat[List[String]] {

  override def writes(clazz: List[String]): JsObject = Json.obj(
    sMessages -> clazz
  )

  override def reads(json: JsValue): JsResult[List[String]] =
    (json \ sMessages).validate(Reads.list[String])

}
