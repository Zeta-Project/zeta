package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue


object Method {

  case class Declaration(
      name: String,
      parameters: Seq[Parameter]
  )

  case class Parameter(
      name: String,
      typ: AttributeType
  )

  case class Implementation(
      code: String,
      returnType: Option[AttributeType]
  )

  implicit val declarationPlayJsonFormat: Format[Declaration] = Json.format[Declaration]

  implicit val implementationPlayJsonFormat: Format[Implementation] = Json.format[Implementation]

  implicit val parameterPlayJsonFormat: Format[Parameter] = Json.format[Parameter]

  implicit val methodsPlayJsonFormat = new Format[Map[Declaration, Implementation]] {

    private val sString = "string"
    private val sBool = "bool"
    private val sInt = "int"
    private val sDouble = "double"

    private val sName = "name"
    private val sParameters = "parameters"
    private val sType = "type"

    override def writes(map: Map[Declaration, Implementation]): JsValue = {
      val a = map.toList.map(e => JsObject(Map(
        sName -> JsString(e._1.name),
        sParameters -> JsArray(e._1.parameters.map(parameter => JsObject(Map(
          sName -> JsString(parameter.name),
          sType -> AttributeType.playJsonFormat.writes(parameter.typ)))))

      )))
      null // TODO
    }

    override def reads(json: JsValue): JsResult[Map[Declaration, Implementation]] = {

      /* json match {
        case JsString(`sString`) => JsSuccess(StringType)
        case JsString(`sBool`) => JsSuccess(BoolType)
        case JsString(`sInt`) => JsSuccess(IntType)
        case JsString(`sDouble`) => JsSuccess(DoubleType)
        case _ => MEnum.playJsonFormat.reads(json)
      } */
      null // TODO
    }

  }


}
