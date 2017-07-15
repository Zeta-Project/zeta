package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
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

  private implicit val methodKeyPlayJsonFormat: Format[(Declaration, Implementation)] = Json.format[(Declaration, Implementation)]

  implicit val methodsPlayJsonFormat = new Format[Map[Declaration, Implementation]] {

    private val sDeclaration = "declaration"
    private val sImplementation = "implementation"

    override def writes(value: Map[Declaration, Implementation]): JsValue = {
      JsArray(value.map(entry => JsObject(Map(
        sDeclaration -> declarationPlayJsonFormat.writes(entry._1),
        sImplementation -> implementationPlayJsonFormat.writes(entry._2)
      ))).toSeq)
    }

    override def reads(json: JsValue): JsResult[Map[Declaration, Implementation]] = {
      json.validate[JsArray].map(_.value.map { value =>
        val obj = value.validate[JsObject]
        val declaration = obj.map(_.value(sDeclaration).as[Declaration]).get
        val implementation = obj.map(_.value(sImplementation).as[Implementation]).get
        (declaration, implementation)
      }.toMap)
    }
  }

}
