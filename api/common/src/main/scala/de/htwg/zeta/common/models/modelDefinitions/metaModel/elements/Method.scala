package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes


case class Method(
    name: String,
    parameters: SortedMap[String, AttributeType],
    description: String,
    returnType: AttributeType,
    code: String
)

object Method {

  trait MethodMap {

    val methods: Seq[Method]

    /** Methods mapped to their own names. */
    final val methodMap: Map[String, Method] = methods.map(method => (method.name, method)).toMap

  }

}
