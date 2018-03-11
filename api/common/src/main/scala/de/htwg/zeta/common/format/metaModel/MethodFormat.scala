package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.ListMap

import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.Method
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class MethodFormat(
    attributeTypeFormat: AttributeTypeFormat,
    sName: String = "name",
    sParameters: String = "parameters",
    sType: String = "type",
    sDescription: String = "description",
    sReturnType: String = "returnType",
    sCode: String = "code"
) extends OFormat[Method] {

  override def writes(method: Method): JsObject = Json.obj(
    sName -> method.name,
    sParameters -> writesParameters(method.parameters),
    sDescription -> method.description,
    sReturnType -> attributeTypeFormat.writes(method.returnType),
    sCode -> method.code
  )

  private def writesParameters(parameters: ListMap[String, AttributeType]): JsArray = JsArray(
    parameters.map { case (name, typ) => Json.obj(
      sName -> name,
      sType -> attributeTypeFormat.writes(typ)
    )
    }.toList
  )

  override def reads(json: JsValue): JsResult[Method] = for {
    name <- (json \ sName).validate[String]
    parameters <- (json \ sParameters).validate(readsParameters)
    description <- (json \ sDescription).validate[String]
    returnType <- (json \ sReturnType).validate(attributeTypeFormat)
    code <- (json \ sCode).validate[String]
  } yield {
    Method(name, parameters, description, returnType, code)
  }

  private def readsParameters: Reads[ListMap[String, AttributeType]] = Reads { json =>
    json.validate(Reads.list(readsParameter)).map(ListMap(_: _*))
  }

  private def readsParameter: Reads[(String, AttributeType)] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      typ <- (json \ sType).validate(attributeTypeFormat)
    } yield {
      (name, typ)
    }
  }

}
