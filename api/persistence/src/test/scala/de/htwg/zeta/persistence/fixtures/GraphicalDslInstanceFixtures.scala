package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.Node
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance

object GraphicalDslInstanceFixtures {

  val nodeName1: String = "nodeName1"
  val nodeName2: String = "nodeName2"

  val edgeName1: String = "edgeName1"
  val edgeName2: String = "edgeName2"

  val node1 = NodeInstance(
    name = "node1",
    className = GraphicalDslFixtures.class1.name,
    outputEdgeNames = Seq(edgeName1, edgeName2),
    inputEdgeNames = Seq.empty,
    attributes = Seq.empty,
    attributeValues = Map("attribute1" -> IntValue(1)),
    methods = Seq.empty
  )

  val node2 = NodeInstance(
    name = "node2",
    className = GraphicalDslFixtures.class2.name,
    outputEdgeNames = Seq.empty,
    inputEdgeNames = Seq(edgeName1, edgeName2),
    attributes = Seq.empty,
    attributeValues = Map("attribute2" -> DoubleValue(2)),
    methods = Seq.empty
  )

  val edge1 = EdgeInstance(
    name = "edge1",
    referenceName = GraphicalDslFixtures.reference1.name,
    sourceNodeName = nodeName1,
    targetNodeName = nodeName2,
    attributes = Seq.empty,
    attributeValues = Map("attribute3" -> IntValue(-1)),
    methods = Seq.empty
  )

  val edge2 = EdgeInstance(
    name = "edge2",
    referenceName = GraphicalDslFixtures.reference2.name,
    sourceNodeName = nodeName2,
    targetNodeName = nodeName1,
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty
  )

  val entity1 = GraphicalDslInstance(
    id = UUID.randomUUID(),
    name = "name1",
    graphicalDslId = GraphicalDslFixtures.entity1.id,
    nodes = Seq(node1, node2),
    edges = Seq(edge1, edge2),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty,
    uiState = "uiState1"
  )

  val entity2 = GraphicalDslInstance(
    id = UUID.randomUUID(),
    name = "name2",
    graphicalDslId = GraphicalDslFixtures.entity2.id,
    nodes = Seq(node1),
    edges = Seq(edge1),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty,
    uiState = "uiState2"
  )

  val entity2Updated: GraphicalDslInstance = entity2.copy(name = "name2Updated")

  val entity3 = GraphicalDslInstance(
    id = UUID.randomUUID(),
    name = "name3",
    graphicalDslId = GraphicalDslFixtures.entity2.id,
    nodes = Seq(node2),
    edges = Seq(edge1),
    attributes = Seq.empty,
    attributeValues = Map.empty,
    methods = Seq.empty,
    uiState = "uiState3"
  )

}
