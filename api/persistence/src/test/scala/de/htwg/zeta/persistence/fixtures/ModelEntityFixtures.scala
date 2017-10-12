package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink

object ModelEntityFixtures {

  val nodeId1: String = "nodeName1"
  val nodeId2: String = "nodeName2"

  val edgeId1: String = "edgeName1"
  val edgeId2: String = "edgeName2"

  val toNodes1 = NodeLink(
    className = MetaModelEntityFixtures.class1.name,
    nodeNames = Seq(nodeId1, nodeId2)
  )

  val toNodes2 = NodeLink(
    className = MetaModelEntityFixtures.class2.name,
    nodeNames = Seq.empty
  )

  val toEdges1 = EdgeLink(
    referenceName = MetaModelEntityFixtures.reference1.name,
    edgeNames = Seq(edgeId1)
  )

  val toEdges2 = EdgeLink(
    referenceName = MetaModelEntityFixtures.reference2.name,
    edgeNames = Seq(edgeId2)
  )

  val node1 = Node(
    name = "node1",
    className = MetaModelEntityFixtures.class1.name,
    outputs = Seq(toEdges1, toEdges2),
    inputs = Seq.empty,
    attributes = Seq.empty,
    attributeValues = Map("attribute1" -> Seq(IntValue(1))),
    methods = Seq.empty
  )

  val node2 = Node(
    name = "node2",
    className = MetaModelEntityFixtures.class2.name,
    outputs = Seq.empty,
    inputs = Seq(toEdges1, toEdges2),
    attributes = Seq.empty,
    attributeValues = Map("attribute2" -> Seq(DoubleValue(2), DoubleValue(3))),
    methods = Seq.empty
  )

  val edge1 = Edge(
    name = "edge1",
    referenceName = MetaModelEntityFixtures.reference1.name,
    source = Seq(toNodes1, toNodes2),
    target = Seq.empty,
    attributes = Seq.empty,
    attributeValues = Map("attribute3" -> Seq(IntValue(-1))),
    methods = Seq.empty
  )

  val edge2 = Edge(
    name = "edge2",
    referenceName = MetaModelEntityFixtures.reference2.name,
    source = Seq.empty,
    target = Seq(toNodes1, toNodes2),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty
  )

  val model1 = Model(
    name = "model1",
    metaModelId = MetaModelEntityFixtures.entity1.id,
    nodes = Seq(node1, node2),
    edges = Seq(edge1, edge2),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty,
    uiState = "uiState1"
  )

  val model2 = Model(
    name = "model2",
    metaModelId = MetaModelEntityFixtures.entity2.id,
    nodes = Seq(node1),
    edges = Seq(edge1),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty,
    uiState = "uiState2"
  )

  val entity1 = ModelEntity(
    id = UUID.randomUUID(),
    model = model1
  )

  val entity2 = ModelEntity(
    id = UUID.randomUUID(),
    model = model2
  )

  val entity2Updated: ModelEntity = entity2.copy(model = entity2.model.copy(uiState = "updatedState"))

  val entity3 = ModelEntity(
    id = UUID.randomUUID(),
    model = model1
  )

}
