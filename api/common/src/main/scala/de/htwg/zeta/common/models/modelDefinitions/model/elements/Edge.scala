package de.htwg.zeta.common.models.modelDefinitions.model.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.Format
import play.api.libs.json.Json


/** Represents an MReference type instance.
 *
 * @param name       the name of the edge
 * @param reference  the MReference instance that represents the edge's type
 * @param source     the nodes that are the origin of relationships
 * @param target     the nodes that can be reached
 * @param attributes a map with attribute names and the assigned values
 */
case class Edge(
    name: String,
    reference: MReference,
    source: Set[ToNodes],
    target: Set[ToNodes],
    attributes: Map[String, Set[AttributeValue]]
) extends ModelElement with HasAttributes

object Edge {

  implicit val playJsonEdgeFormat: Format[Edge] = Json.format[Edge]

}
