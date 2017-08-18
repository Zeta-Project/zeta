package de.htwg.zeta.common.models.modelDefinitions.model.elements

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Represents nodes in reach of an edge.
 *
 * @param className the name of the MClass instance that represents the type of the nodes
 * @param nodeIds the names of the nodes
 */
case class NodeLink(className: String, nodeIds: Seq[UUID]) extends Link

object NodeLink {

  def playJsonReads(metaModel: MetaModel): Reads[NodeLink] = new Reads[NodeLink] {
    override def reads(json: JsValue): JsResult[NodeLink] = {
      for {
        clazz <- (json \ "className").validate[String].map(metaModel.classMap)
        nodeIds <- (json \ "nodeIds").validate[List[UUID]]
      } yield {
        NodeLink(clazz.name, nodeIds)
      }
    }
  }

  implicit val playJsonWrites: Writes[NodeLink] = Json.writes[NodeLink]

}
