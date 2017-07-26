package de.htwg.zeta.common.models.modelDefinitions.model.elements

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
 * @param nodeNames the names of the nodes
 */
case class ToNodes(className: String, nodeNames: Seq[String]) extends Link

object ToNodes {

  def playJsonReads(metaModel: MetaModel): Reads[ToNodes] = new Reads[ToNodes] {
    override def reads(json: JsValue): JsResult[ToNodes] = {
      for {
        clazz <- (json \ "className").validate[String].map(metaModel.classMap)
        nodeNames <- (json \ "nodeNames").validate[List[String]]
      } yield {
        ToNodes(clazz.name, nodeNames)
      }
    }
  }

  implicit val playJsonWrites: Writes[ToNodes] = Json.writes[ToNodes]

}
