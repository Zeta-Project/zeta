package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

object ModelEntityFixtures {

  val nodeName1: String = "nodeName1"
  val nodeName2: String = "nodeName2"

  val edgeName1: String = "edgeName1"
  val edgeName2: String = "edgeName2"

  val node1 = Node(
    name = "node1",
    className = MetaModelEntityFixtures.class1.name,
    outputEdgeNames = Seq(edgeName1, edgeName2),
    inputEdgeNames = Seq.empty,
    attributes = Seq.empty,
    attributeValues = Map("attribute1" -> IntValue(1)),
    methods = Seq.empty
  )

  val node2 = Node(
    name = "node2",
    className = MetaModelEntityFixtures.class2.name,
    outputEdgeNames = Seq.empty,
    inputEdgeNames = Seq(edgeName1, edgeName2),
    attributes = Seq.empty,
    attributeValues = Map("attribute2" -> DoubleValue(2)),
    methods = Seq.empty
  )

  val edge1 = Edge(
    name = "edge1",
    referenceName = MetaModelEntityFixtures.reference1.name,
    sourceNodeName = nodeName1,
    targetNodeName = nodeName2,
    attributes = Seq.empty,
    attributeValues = Map("attribute3" -> IntValue(-1)),
    methods = Seq.empty
  )

  val edge2 = Edge(
    name = "edge2",
    referenceName = MetaModelEntityFixtures.reference2.name,
    sourceNodeName = nodeName2,
    targetNodeName = nodeName1,
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
