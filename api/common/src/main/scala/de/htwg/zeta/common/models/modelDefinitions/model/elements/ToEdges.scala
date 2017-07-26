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
case class ToEdges(referenceName: String, edgeIds: Seq[UUID]) extends Link

object ToEdges {

  def playJsonReads(metaModel: MetaModel): Reads[ToEdges] = {
    new Reads[ToEdges] {
      override def reads(json: JsValue): JsResult[ToEdges] = {
        for {
          reference <- (json \ "referenceName").validate[String].map(metaModel.referenceMap)
          edgeIds <- (json \ "edgeIds").validate[List[UUID]]
        } yield {
          ToEdges(reference.name, edgeIds)
        }
      }
    }
  }

  implicit val playJsonWrites: Writes[ToEdges] = Json.writes[ToEdges]

}
