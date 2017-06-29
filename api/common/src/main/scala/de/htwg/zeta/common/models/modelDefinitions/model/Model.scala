package de.htwg.zeta.common.models.modelDefinitions.model

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.Json
import play.api.libs.json.OFormat

/** Immutable container for model definitions
 *
 * @param name      the name of the model
 * @param metaModel the corresponding MetaModel instance
 * @param nodes     the nodes of the actual model data
 * @param edges     the edges of the actual model data
 * @param uiState   the ui-state of the browser client. Location is debatable
 */
case class Model(
    name: String,
    metaModel: MetaModel,
    nodes: Seq[Node],
    edges: Seq[Edge],
    uiState: String
) {

  /** Nodes mapped to their own names. */
  val nodeMap: Map[String, Node] = nodes.map(node => (node.name, node)).toMap

  /** Edges mapped to their own names. */
  val edgeMap: Map[String, Edge] = edges.map(edge => (edge.name, edge)).toMap

}

object Model {

  implicit val playJsonModelFormat: OFormat[Model] = Json.format[Model]

}
