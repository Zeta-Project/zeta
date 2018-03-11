package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class EnumFormat(
    sName: String = "name",
    sValueNames: String = "valueNames"
) extends OFormat[MEnum] {

  override def writes(enum: MEnum): JsObject = Json.obj(
    sName -> enum.name,
    sValueNames -> Writes.seq[String].writes(enum.valueNames)
  )

  override def reads(json: JsValue): JsResult[MEnum] = for {
    name <- (json \ sName).validate[String]
    valueNames <- (json \ sValueNames).validate(Reads.list[String])
  } yield {
    MEnum(name, valueNames)
  }

}
