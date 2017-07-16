package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter
import play.api.libs.json.Format
import play.api.libs.json.Json


case class Method(
    name: String,
    parameters: Seq[Parameter],
    returnType: Option[AttributeType],
    code: String
)

object Method {

  case class Parameter(
      name: String,
      typ: AttributeType
  )

  implicit val playJsonFormat: Format[Method] = Json.format[Method]

  implicit val parameterPlayJsonFormat: Format[Parameter] = Json.format[Parameter]

}
