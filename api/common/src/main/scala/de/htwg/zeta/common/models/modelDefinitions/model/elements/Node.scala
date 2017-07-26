package de.htwg.zeta.common.models.modelDefinitions.model.elements

import java.util.UUID

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
 * @param id         the name of the node
 * @param className  the name of the MClass instance that represents the node's type
 * @param outputs    the outgoing edges
 * @param inputs     the incoming edges
 * @param attributes a map with attribute names and the assigned values
 */
case class Node(
    id: UUID,
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
          id <- (json \ "id").validate[UUID]
          clazz <- (json \ "className").validate[String].map(metaModel.classMap)
          outputs <- (json \ "outputs").validate(Reads.map[List[UUID]])
          inputs <- (json \ "inputs").validate(Reads.map[List[UUID]])
          attributes <- (json \ "attributes").validate(AttributeValue.playJsonReads(metaModel, clazz.attributes))
        } yield {
          Node(
            id = id,
            className = clazz.name,
            outputs = outputs.map(e => ToEdges(e._1, e._2)).toList,
            inputs = inputs.map(e => ToEdges(e._1, e._2)).toList,
            attributes = attributes
          )
        }
      }
    }
  }

}
