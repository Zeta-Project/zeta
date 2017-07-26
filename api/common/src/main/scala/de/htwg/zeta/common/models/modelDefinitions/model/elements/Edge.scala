package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Represents an MReference type instance.
 *
 * @param name          the name of the edge
 * @param referenceName the name of the MReference instance that represents the edge's type
 * @param source        the nodes that are the origin of relationships
 * @param target        the nodes that can be reached
 * @param attributes    a map with attribute names and the assigned values
 */
case class Edge(
    name: String,
    referenceName: String,
    source: Seq[ToNodes],
    target: Seq[ToNodes],
    attributes: Map[String, Seq[AttributeValue]]
) extends ModelElement with HasAttributes

object Edge {

  def playJsonReads(metaModel: MetaModel): Reads[Edge] = {
    new Reads[Edge] {
      override def reads(json: JsValue): JsResult[Edge] = {
        for {
          name <- (json \ "name").validate[String]
          reference <- (json \ "referenceName").validate[String].map(metaModel.referenceMap)
          source <- (json \ "source").validate(Reads.list(ToNodes.playJsonReads(metaModel)))
          target <- (json \ "target").validate(Reads.list(ToNodes.playJsonReads(metaModel)))
          attributes <- (json \ "attributes").validate(AttributeValue.playJsonReads(metaModel, reference.attributes))
        } yield {
          Edge(
            name = name,
            referenceName = reference.name,
            source = source,
            target = target,
            attributes = attributes
          )
        }
      }
    }
  }

  implicit val playJsonWrites: Writes[Edge] = Json.writes[Edge]

}
