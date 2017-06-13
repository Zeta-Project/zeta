package models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.MClass
import play.api.libs.json.Format
import play.api.libs.json.Json

/** Represents an MClass type instance.
 *
 * @param name       the name of the node
 * @param clazz      the MClass instance that represents the node's type
 * @param outputs    the outgoing edges
 * @param inputs     the incoming edges
 * @param attributes a map with attribute names and the assigned values
 */
case class Node(
    name: String,
    clazz: MClass,
    outputs: Seq[ToEdges],
    inputs: Seq[ToEdges],
    attributes: Map[String, Seq[AttributeValue]]
) extends ModelElement with HasAttributes

object Node {

  implicit val playJsonNodeFormat: Format[Node] = Json.format[Node]

}