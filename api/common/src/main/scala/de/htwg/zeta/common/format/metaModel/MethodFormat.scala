package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.format.metaModel.MethodFormat.sCode
import de.htwg.zeta.common.format.metaModel.MethodFormat.sDescription
import de.htwg.zeta.common.format.metaModel.MethodFormat.sName
import de.htwg.zeta.common.format.metaModel.MethodFormat.sParameters
import de.htwg.zeta.common.format.metaModel.MethodFormat.sReturnType
import de.htwg.zeta.common.format.metaModel.MethodFormat.sType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


object MethodFormat extends OWrites[Method] {

  private val sName = "name"
  private val sParameters = "parameters"
  private val sType = "type"
  private val sDescription = "description"
  private val sReturnType = "returnType"
  private val sCode = "code"

  override def writes(method: Method): JsObject = Json.obj(
    sName -> method.name,
    sParameters -> writesParameters(method.parameters),
    sDescription -> method.description,
    sReturnType -> AttributeTypeFormat.writes(method.returnType),
    sCode -> method.code
  )

  private def writesParameters(parameters: SortedMap[String, AttributeType]): JsArray = JsArray(
    parameters.map { case (name, typ) => Json.obj(
      sName -> name,
      sType -> AttributeTypeFormat.writes(typ)
    )}.toList
  )

}

class MethodFormat(enums: Seq[MEnum]) extends Reads[Method] {

  override def reads(json: JsValue): JsResult[Method] = {
    for {
      name <- (json \ sName).validate[String]
      parameters <- (json \ sParameters).validate(readsParameters)
      description <- (json \ sDescription).validate[String]
      returnType <- (json \ sReturnType).validate(new AttributeTypeFormat(enums))
      code <- (json \ sCode).validate[String]
    } yield {
      Method(name, parameters, description, returnType, code)
    }
  }

  private def readsParameters: Reads[SortedMap[String, AttributeType]] = Reads { json =>
    json.validate(Reads.list(readsParameter)).map(SortedMap(_: _*))
  }

  private def readsParameter: Reads[(String, AttributeType)] = Reads { json =>
    for {
      name <- (json \ sName).validate[String]
      typ <- (json \ sType).validate(new AttributeTypeFormat(enums))
    } yield {
      (name, typ)
    }
  }

}
