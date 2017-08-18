package de.htwg.zeta.common.models.modelDefinitions.model.elements

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/** Represents outgoing edges of a node
 *
 * @param referenceName the name of the MReference instance that represents the type of the references
 * @param edgeIds       the names of the linked edges
 */
case class EdgeLink(referenceName: String, edgeIds: Seq[UUID]) extends Link

object EdgeLink {

  def playJsonReads(metaModel: MetaModel): Reads[EdgeLink] = {
    new Reads[EdgeLink] {
      override def reads(json: JsValue): JsResult[EdgeLink] = {
        for {
          reference <- (json \ "referenceName").validate[String].map(metaModel.referenceMap)
          edgeIds <- (json \ "edgeIds").validate[List[UUID]]
        } yield {
          EdgeLink(reference.name, edgeIds)
        }
      }
    }
  }

  implicit val playJsonWrites: Writes[EdgeLink] = Json.writes[EdgeLink]

}
