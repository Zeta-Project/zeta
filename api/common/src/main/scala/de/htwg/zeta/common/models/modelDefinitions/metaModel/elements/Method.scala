package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.Parameter
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


case class Method(
    name: String,
    parameters: Seq[Parameter],
    description: String,
    returnType: Option[AttributeType],
    code: String
)

object Method {

  private val sName = "name"

  case class Parameter(
      name: String,
      typ: AttributeType
  )

  object Parameter {

    def playJsonReads(enums: Seq[MEnum]): Reads[Parameter] = new Reads[Parameter] {
      override def reads(json: JsValue): JsResult[Parameter] = {
        for {
          name <- (json \ sName).validate[String]
          typ <- (json \ "typ").validate(AttributeType.playJsonReads(enums))
        } yield {
          Parameter(name, typ)
        }
      }
    }

    implicit val playJsonWrites: Writes[Parameter] = Json.writes[Parameter]
  }

  def playJsonReads(enums: Seq[MEnum]): Reads[Method] = new Reads[Method] {
    override def reads(json: JsValue): JsResult[Method] = {
      for {
        name <- (json \ sName).validate[String]
        parameters <- (json \ "parameters").validate(Reads.list(Parameter.playJsonReads(enums)))
        description <- (json \ "description").validate[String]
        returnType <- (json \ "returnType").validate(Reads.optionNoError(AttributeType.playJsonReads(enums)))
        code <- (json \ "code").validate[String]
      } yield {
        Method(name, parameters, description, returnType, code)
      }
    }
  }

  implicit val playJsonWrites: Writes[Method] = Json.writes[Method]



}
