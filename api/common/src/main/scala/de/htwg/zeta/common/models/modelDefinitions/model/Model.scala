package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.Format
import play.api.libs.json.Json

/** Immutable container for model definitions
 *
 * @param name        the name of the model
 * @param metaModelId the id for the corresponding MetaModel
 * @param nodes       the nodes of the actual model data
 * @param edges       the edges of the actual model data
 * @param uiState     the ui-state of the browser client. Location is debatable
 */
case class Model(
    name: String,
    metaModelId: UUID,
    nodes: Seq[Node],
    edges: Seq[Edge],
    uiState: String
) {

  /** Nodes mapped to their own names. */
  val nodeMap: Map[String, Node] = Option(nodes).fold(
    Map.empty[String, Node]
  ) { nodes =>
    nodes.filter(Option(_).isDefined).map(node => (node.name, node)).toMap
  }

  /** Edges mapped to their own names. */
  val edgeMap: Map[String, Edge] = Option(edges).fold(
    Map.empty[String, Edge]
  ) { edges =>
    edges.filter(Option(_).isDefined).map(edge => (edge.name, edge)).toMap
  }

}

object Model {

  implicit val playJsonModelFormat: Format[Model] = Json.format[Model]

}
