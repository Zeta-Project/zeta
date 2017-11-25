package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Represents nodes in reach of an edge.
 *
 * @param className the name of the MClass instance that represents the type of the nodes
 * @param nodeNames the names of the nodes
 */
case class NodeLink(className: String, nodeNames: Seq[String]) extends Link

object NodeLink {

  def playJsonReads(metaModel: MetaModel): Reads[NodeLink] = Reads { json =>
    for {
      clazz <- (json \ "className").validate[String].map(metaModel.classMap)
      nodeNames <- (json \ "nodeNames").validate[List[String]]
    } yield {
      NodeLink(clazz.name, nodeNames)
    }
  }

  implicit val playJsonWrites: Writes[NodeLink] = Json.writes[NodeLink]

}
