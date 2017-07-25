package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes

object ModelEntityFixtures {

  val nodeName1 = "nodeName1"
  val nodeName2 = "nodeName2"

  val edgeName1 = "edgeName1"
  val edgeName2 = "edgeName2"

  val toNodes1 = ToNodes(
    className = MetaModelEntityFixtures.class1.name,
    nodeNames = Seq(nodeName1, nodeName2)
  )

  val toNodes2 = ToNodes(
    className = MetaModelEntityFixtures.class2.name,
    nodeNames = Seq.empty
  )

  val toEdges1 = ToEdges(
    referenceName = MetaModelEntityFixtures.reference1.name,
    edgeNames = Seq(edgeName1)
  )

  val toEdges2 = ToEdges(
    referenceName = MetaModelEntityFixtures.reference2.name,
    edgeNames = Seq(edgeName2)
  )

  val node1 = Node(
    name = "name1",
    className = MetaModelEntityFixtures.class1.name,
    outputs = Seq(toEdges1, toEdges2),
    inputs = Seq.empty,
    attributes = Map("attribute1" -> Seq(MInt(1)))
  )

  val node2 = Node(
    name = "name2",
    className = MetaModelEntityFixtures.class2.name,
    outputs = Seq.empty,
    inputs = Seq(toEdges1, toEdges2),
    attributes = Map("attribute2" -> Seq(MDouble(2), MDouble(3)))
  )

  val edge1 = Edge(
    name = "edge1",
    referenceName = MetaModelEntityFixtures.reference1.name,
    source = Seq(toNodes1, toNodes2),
    target = Seq.empty,
    attributes = Map("attribute3" -> Seq(MInt(-1)))
  )

  val edge2 = Edge(
    name = "edge2",
    referenceName = MetaModelEntityFixtures.reference2.name,
    source = Seq.empty,
    target = Seq(toNodes1, toNodes2),
    attributes = Map.empty
  )

  val model1 = Model(
    name = "model1",
    metaModelId = MetaModelEntityFixtures.entity1.id,
    nodes = Seq(node1, node2),
    edges = Seq(edge1, edge2),
    attributes = Map.empty,
    uiState = "uiState1"
  )

  val model2 = Model(
    name = "model2",
    metaModelId = MetaModelEntityFixtures.entity2.id,
    nodes = Seq(node1),
    edges = Seq(edge1),
    attributes = Map.empty,
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
