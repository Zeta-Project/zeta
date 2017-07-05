package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.Json

case class MethodImplementation(
    code: String,
    returnType: Option[AttributeType]
)

object MethodImplementation {

  implicit val playJsonFormat: Format[MethodImplementation] = Json.format[MethodImplementation]

}