package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge.EdgeMap
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node.NodeMap


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
    attributes: Seq[MAttribute],
    attributeValues: Map[String, AttributeValue],
    methods: Seq[Method],
    uiState: String
) extends NodeMap with EdgeMap with AttributeMap with MethodMap

object Model {

  def empty(name: String, metaModelId: UUID): Model = {
    Model(
      name = name,
      metaModelId = metaModelId,
      nodes = Seq.empty,
      edges = Seq.empty,
      attributes = Seq.empty,
      attributeValues = Map.empty,
      methods = Seq.empty,
      uiState = ""
    )
  }

}
