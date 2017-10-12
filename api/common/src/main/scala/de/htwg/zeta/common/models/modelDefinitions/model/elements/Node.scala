package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.HasAttributeValues
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/** Represents an MClass type instance.
 *
 * @param name            the name of the node
 * @param className       the name of the MClass instance that represents the node's type
 * @param outputs         the outgoing edges
 * @param inputs          the incoming edges
 * @param attributeValues a map with attribute names and the assigned values
 */
case class Node(
    name: String,
    className: String,
    outputs: Seq[EdgeLink],
    inputs: Seq[EdgeLink],
    attributes: Seq[MAttribute],
    attributeValues: Map[String, Seq[AttributeValue]],
    methods: Seq[Method]
) extends ModelElement with AttributeMap with HasAttributeValues with MethodMap

object Node {

  trait NodeMap {

    val nodes: Seq[Node]

    /** Nodes mapped to their own names. */
    final val nodeMap: Map[String, Node] = Option(nodes).fold(
      Map.empty[String, Node]
    ) { nodes =>
      nodes.filter(Option(_).isDefined).map(node => (node.name, node)).toMap
    }

  }

  implicit val playJsoWrites: Writes[Node] = Json.writes[Node]

  def playJsonReads(metaModel: MetaModel): Reads[Node] = {
    new Reads[Node] {
      override def reads(json: JsValue): JsResult[Node] = {
        for {
          name <- (json \ "name").validate[String]
          clazz <- (json \ "className").validate[String].map(metaModel.classMap)
          outputs <- (json \ "outputs").validate(Reads.map[List[String]])
          inputs <- (json \ "inputs").validate(Reads.map[List[String]])
          attributes <- (json \ "attributes").validate(Reads.list(MAttribute.playJsonReads(metaModel.enums)))
          attributeValues <- (json \ "attributeValues").validate(AttributeValue.playJsonReads(metaModel, clazz.attributes, attributes))
          methods <- (json \ "methods").validate(Reads.list(Method.playJsonReads(metaModel.enums)))
        } yield {
          Node(
            name = name,
            className = clazz.name,
            outputs = outputs.map(e => EdgeLink(e._1, e._2)).toList,
            inputs = inputs.map(e => EdgeLink(e._1, e._2)).toList,
            attributes = attributes,
            attributeValues = attributeValues,
            methods = methods
          )
        }
      }
    }
  }

}
