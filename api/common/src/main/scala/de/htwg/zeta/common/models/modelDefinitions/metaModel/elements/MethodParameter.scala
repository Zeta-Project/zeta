package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.Json

case class MethodParameter(
    name: String,
    typ: AttributeType
)

object MethodParameter {

  implicit val playJsonFormat: Format[MethodParameter] = Json.format[MethodParameter]

}