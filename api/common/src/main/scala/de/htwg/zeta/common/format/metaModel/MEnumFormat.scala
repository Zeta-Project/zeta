package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

object MEnumFormat extends OFormat[MEnum] {

  private val sName = "name"
  private val sValueNames = "valueNames"

  override def writes(enum: MEnum): JsObject = Json.obj(
    sName -> enum.name,
    sValueNames -> JsArray(enum.valueNames.map(JsString))
  )

  override def reads(json: JsValue): JsResult[MEnum] = {
    for {
      name <- (json \ sName).validate[String]
      valueNames <- (json \ sValueNames).validate(Reads.list)
    } yield {
      MEnum(name, valueNames)
    }
  }

}