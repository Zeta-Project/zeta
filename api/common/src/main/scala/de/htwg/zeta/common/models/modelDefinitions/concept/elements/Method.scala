package de.htwg.zeta.common.models.modelDefinitions.concept.elements

import scala.collection.immutable.ListMap
import scala.collection.immutable.Seq


case class Method(
    name: String,
    parameters: ListMap[String, AttributeType],
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
