package de.htwg.zeta.common.models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import play.api.libs.json.Format
import play.api.libs.json.Json


/** Represents nodes in reach of an edge.
 *
 * @param className the name of the MClass instance that represents the type of the nodes
 * @param nodeNames the names of the nodes
 */
case class ToNodes(className: String, nodeNames: Seq[String]) extends Link

object ToNodes {

  implicit val playJsonToNodesFormat: Format[ToNodes] = Json.format[ToNodes]

}
