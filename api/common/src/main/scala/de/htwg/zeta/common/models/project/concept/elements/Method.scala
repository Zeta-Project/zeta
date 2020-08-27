package de.htwg.zeta.common.models.project.concept.elements

import scala.collection.immutable.ListMap
import scala.collection.immutable.Seq

import play.api.libs.json.JsObject
import play.api.libs.json.Json


case class Method(
    name: String,
    parameters: ListMap[String, AttributeType],
    description: String,
    returnType: AttributeType,
    code: String
) {
  def asJson: JsObject = Json.obj(
    "name" -> name,
    "parameters" -> parameters.mapValues(_.asJson),
    "description" -> description,
    "returnType" -> returnType.asJson,
    "code" -> code
  )
}

object Method {

  trait MethodMap {

    val methods: Seq[Method]

    /** Methods mapped to their own names. */
    final val methodMap: Map[String, Method] = methods.map(method => (method.name, method)).toMap

  }

}
