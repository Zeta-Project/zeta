package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/** Represents outgoing edges of a node
 *
 * @param referenceName the name of the MReference instance that represents the type of the references
 * @param edgeNames     the names of the linked edges
 */
case class EdgeLink(referenceName: String, edgeNames: Seq[String]) extends Link

object EdgeLink {

  def playJsonReads(metaModel: MetaModel): Reads[EdgeLink] = Reads { json =>
    for {
      reference <- (json \ "referenceName").validate[String].map(metaModel.referenceMap)
      edgeNames <- (json \ "edgeNames").validate[List[String]]
    } yield {
      EdgeLink(reference.name, edgeNames)
    }
  }

  implicit val playJsonWrites: Writes[EdgeLink] = Json.writes[EdgeLink]

}
