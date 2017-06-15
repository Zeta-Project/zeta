package de.htwg.zeta.common.models.modelDefinitions.model.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import play.api.libs.json.Format
import play.api.libs.json.Json


/** Represents nodes in reach of an edge.
 *
 * @param clazz     the MClass instance that represents the type of the nodes
 * @param nodeNames the names of the nodes
 */
case class ToNodes(clazz: MClass, nodeNames: Set[String]) extends Link

object ToNodes {

  implicit val playJsonToNodesFormat: Format[ToNodes] = Json.format[ToNodes]

}
