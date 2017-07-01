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
    clazz = MetaModelEntityFixtures.class1,
    nodeNames = Seq(nodeName1, nodeName2)
  )

  val toNodes2 = ToNodes(
    clazz = MetaModelEntityFixtures.class2,
    nodeNames = Seq.empty
  )

  val toEdges1 = ToEdges(
    reference = MetaModelEntityFixtures.reference1,
    edgeNames = Seq(edgeName1)
  )

  val toEdges2 = ToEdges(
    reference = MetaModelEntityFixtures.reference2,
    edgeNames = Seq(edgeName2)
  )

  val node1 = Node(
    name = "name1",
    clazz = MetaModelEntityFixtures.class1,
    outputs = Seq(toEdges1, toEdges2),
    inputs = Seq.empty,
    attributes = Map("attribute1" -> Seq(MInt(1)))
  )

  val node2 = Node(
    name = "name2",
    clazz = MetaModelEntityFixtures.class2,
    outputs = Seq.empty,
    inputs = Seq(toEdges1, toEdges2),
    attributes = Map("attribute2" -> Seq(MDouble(2), MDouble(3)))
  )

  val edge1 = Edge(
    name = "edge1",
    reference = MetaModelEntityFixtures.reference1,
    source = Seq(toNodes1, toNodes2),
    target = Seq.empty,
    attributes = Map("attribute3" -> Seq(MInt(-1)))
  )

  val edge2 = Edge(
    name = "edge2",
    reference = MetaModelEntityFixtures.reference2,
    source = Seq.empty,
    target = Seq(toNodes1, toNodes2),
    attributes = Map.empty
  )

  val model1 = Model(
    name = "model1",
    metaModelId = MetaModelEntityFixtures.entity1.id,
    nodes = Seq(node1, node2),
    edges = Seq(edge1, edge2),
    uiState = "uiState1"
  )

  val model2 = Model(
    name = "model2",
    metaModelId = MetaModelEntityFixtures.entity2.id,
    nodes = Seq(node1),
    edges = Seq(edge1),
    uiState = "uiState2"
  )

  val entity1 = ModelEntity(
    id = UUID.randomUUID(),
    model = model1,
    metaModelId = UUID.randomUUID(),
    links = Some(MetaModelEntityFixtures.links1)
  )

  val entity2 = ModelEntity(
    id = UUID.randomUUID(),
    model = model2,
    metaModelId = UUID.randomUUID(),
    links = Some(MetaModelEntityFixtures.links2)
  )

  val entity2Updated: ModelEntity = entity2.copy(links = Some(MetaModelEntityFixtures.links1))

  val entity3 = ModelEntity(
    id = UUID.randomUUID(),
    model = model1,
    metaModelId = UUID.randomUUID(),
    links = None
  )

}
