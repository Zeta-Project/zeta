package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/** Represents an MClass type instance.
 *
 * @param name       the name of the node
 * @param className  the name of the MClass instance that represents the node's type
 * @param outputs    the outgoing edges
 * @param inputs     the incoming edges
 * @param attributes a map with attribute names and the assigned values
 */
case class Node(
    name: String,
    className: String,
    outputs: Seq[ToEdges],
    inputs: Seq[ToEdges],
    attributes: Map[String, Seq[AttributeValue]]
) extends ModelElement with HasAttributes

object Node {

  implicit val playJsoWrites: Writes[Node] = Json.writes[Node]

  def playJsonReads(metaModel: MetaModel): Reads[Node] = {
    new Reads[Node] {
      override def reads(json: JsValue): JsResult[Node] = {
        for {
          name <- (json \ "name").validate[String]
          clazz <- (json \ "className").validate[String].map(metaModel.classMap)
          outputs <- (json \ "outputs").validate(Reads.list(ToEdges.playJsonReads(metaModel)))
          inputs <- (json \ "inputs").validate(Reads.list(ToEdges.playJsonReads(metaModel)))
          attributes <- (json \ "attributes").validate(AttributeValue.playJsonReads(metaModel, clazz.attributes))
        } yield {
          Node(
            name = name,
            className = clazz.name,
            outputs = outputs,
            inputs = inputs,
            attributes = attributes
          )
        }
      }
    }
  }

}
