package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge.EdgeMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node.NodeMap


/** A instance of a GraphicalDsl (formerly named Model)
 *
 * @param id             the own id
 * @param name           the own name
 * @param graphicalDslId the id for the corresponding GraphicalDsl
 * @param nodes          the nodes
 * @param edges          the edges
 * @param uiState        the ui-state of the browser client. Location is debatable
 */
case class GraphicalDslInstance(
    id: UUID,
    name: String,
    graphicalDslId: UUID,
    nodes: Seq[Node],
    edges: Seq[Edge],
    attributes: Seq[MAttribute],
    attributeValues: Map[String, AttributeValue],
    methods: Seq[Method],
    uiState: String
) extends Entity with NodeMap with EdgeMap with AttributeMap with MethodMap

object GraphicalDslInstance {

  def empty(name: String, graphicalDslId: UUID): GraphicalDslInstance = {
    GraphicalDslInstance(
      id = UUID.randomUUID(),
      name,
      graphicalDslId,
      nodes = Seq.empty,
      edges = Seq.empty,
      attributes = Seq.empty,
      attributeValues = Map.empty,
      methods = Seq.empty,
      uiState = ""
    )
  }

}