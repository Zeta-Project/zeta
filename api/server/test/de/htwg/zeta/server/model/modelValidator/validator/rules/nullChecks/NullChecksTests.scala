package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NullChecksTests extends FlatSpec with Matchers {

  def nodesToModel(nodes: Seq[Node]): Model = Model.empty("", UUID.randomUUID()).copy(nodes = nodes)

  def edgesToModel(edges: Seq[Edge]): Model = Model.empty("", UUID.randomUUID()).copy(edges = edges)

  val mReference: MReference = MReference.empty("edgeType", Seq.empty, Seq.empty)
  val mClass: MClass = MClass.empty("nodeType")

  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)

  "EdgeAttributesNamesNotNull" should "check for null in edge attributes names" in {
    val rule = new EdgeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null, Seq()), "name" -> Seq())
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNoNullValues" should "check for null in edge attributes values" in {
    val rule = new EdgeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNotNull" should "check for null edge attributes" in {
    val rule = new EdgeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNoNullValues" should "check for null in edge attribute value values" in {
    val rule = new EdgeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(null))
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new EdgeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNoNullValues" should "check for null in edge sources nodes values" in {
    val rule = new EdgeSourcesNodesNoNullValues

    val nonNullSources = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(sourceNodeName = nonNullSources)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(mClass.name, Seq("", null)))
    val nullEdge = emptyEdge.copy(sourceNodeName = nullSources)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNotNull" should "check for null in edge sources nodes" in {
    val rule = new EdgeSourcesNodesNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(sourceNodeName = nonNullSources)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(mClass.name, null))
    val nullEdge = emptyEdge.copy(sourceNodeName = nullSources)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNoNullValues" should "check for null in edge sources values" in {
    val rule = new EdgeSourcesNoNullValues

    val nonNullSources = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(sourceNodeName = nonNullSources)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources: Seq[NodeLink] = Seq(null)
    val nullEdge = emptyEdge.copy(sourceNodeName = nullSources)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNotNull" should "check for null in edge sources" in {
    val rule = new EdgeSourcesNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(sourceNodeName = nonNullSources)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = null
    val nullEdge = emptyEdge.copy(sourceNodeName = nullSources)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesTypeNotNull" should "check for null in edge sources type" in {
    val rule = new EdgeSourcesTypeNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(sourceNodeName = nonNullSources)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(null, Seq("", "")))
    val nullEdge = emptyEdge.copy(sourceNodeName = nullSources)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNoNullValues" should "check for null in edge targets nodes values" in {
    val rule = new EdgeTargetsNodesNoNullValues

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(targetNodeName = nonNullTargets)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(mClass.name, Seq("", null)))
    val nullEdge = emptyEdge.copy(targetNodeName = nullTargets)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNotNull" should "check for null in edge targets nodes" in {
    val rule = new EdgeTargetsNodesNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(targetNodeName = nonNullTargets)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(mClass.name, null))
    val nullEdge = emptyEdge.copy(targetNodeName = nullTargets)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNoNullValues" should "check for null values in edge targets values" in {
    val rule = new EdgeTargetsNoNullValues

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(targetNodeName = nonNullTargets)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets: Seq[NodeLink] = Seq(null)
    val nullEdge = emptyEdge.copy(targetNodeName = nullTargets)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNotNull" should "check for null in edge targets" in {
    val rule = new EdgeTargetsNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(targetNodeName = nonNullTargets)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = null
    val nullEdge = emptyEdge.copy(targetNodeName = nullTargets)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsTypeNotNull" should "check for null in edge targets type" in {
    val rule = new EdgeTargetsTypeNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq("", "")))
    val nonNullEdge = emptyEdge.copy(targetNodeName = nonNullTargets)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(null, Seq("", "")))
    val nullEdge = emptyEdge.copy(targetNodeName = nullTargets)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTypeNotNull" should "check for null in edge type" in {
    val rule = new EdgeTypeNotNull

    val nonNullEdge = emptyEdge
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = emptyEdge.copy(referenceName = null)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsIdNotNull" should "check for null in elements id" in {
    val rule = new ElementsIdNotNull

    val nonNullEdge = emptyEdge
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = emptyEdge.copy(name = null)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNoNullValues" should "check for null in elements values" in {
    val rule = new ElementsNoNullValues

    val nonNullEdge = emptyEdge
    val nonNullModel = Model.empty("", UUID.randomUUID()).copy(edges = Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = null
    val nullModel = Model.empty("", UUID.randomUUID()).copy(edges = Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNotNull" should "check for null in elements" in {
    val rule = new ElementsNotNull

    val nonNullModel = Model.empty("", UUID.randomUUID())
    rule.check(nonNullModel) should be(true)

    val nullModel = Model.empty("", UUID.randomUUID()).copy(nodes = null)
    rule.check(nullModel) should be(false)
  }

  // ------------------------------------------------

  "NodeAttributesNamesNotNull" should "check for null in nodes attributes names" in {
    val rule = new NodeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null: String) -> Seq(), "name" -> Seq())
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNoNullValues" should "check for null in node attributes values" in {
    val rule = new NodeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNotNull" should "check for null node attributes" in {
    val rule = new NodeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNoNullValues" should "check for null in node attribute value values" in {
    val rule = new NodeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(null))
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new NodeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNoNullValues" should "check for null in node input edges values" in {
    val rule = new NodeInputsEdgesNoNullValues

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(inputEdgeNames = nonNullInputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(mReference.name, Seq("", null)))
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNotNull" should "check for null in node input edges" in {
    val rule = new NodeInputsEdgesNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(inputEdgeNames = nonNullInputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(mReference.name, null))
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNoNullValues" should "check for null in node input values" in {
    val rule = new NodeInputsNoNullValues

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(inputEdgeNames = nonNullInputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs: Seq[EdgeLink] = Seq(null)
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNotNull" should "check for null in node inputs" in {
    val rule = new NodeInputsNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(inputEdgeNames = nonNullInputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = null
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsTypeNotNull" should "check for null in node inputs type" in {
    val rule = new NodeInputsTypeNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(inputEdgeNames = nonNullInputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(null, Seq("", "")))
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNoNullValues" should "check for null in node output edges values" in {
    val rule = new NodeOutputsEdgesNoNullValues

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(outputEdgeNames = nonNullOutputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(mReference.name, Seq("", null)))
    val nullNode = emptyNode.copy(outputEdgeNames = nullOutputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNotNull" should "check for null in node output edges" in {
    val rule = new NodeOutputsEdgesNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(outputEdgeNames = nonNullOutputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(mReference.name, null))
    val nullNode = emptyNode.copy(outputEdgeNames = nullOutputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNoNullValues" should "check for null values in node output values" in {
    val rule = new NodeOutputsNoNullValues

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(outputEdgeNames = nonNullOutputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs: Seq[EdgeLink] = Seq(null)
    val nullNode = emptyNode.copy(outputEdgeNames = nullOutputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNotNull" should "check for null in node outputs" in {
    val rule = new NodeOutputsNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(outputEdgeNames = nonNullOutputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = null
    val nullNode = emptyNode.copy(outputEdgeNames = nullOutputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsTypeNotNull" should "check for null in node outputs type" in {
    val rule = new NodeOutputsTypeNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq("", "")))
    val nonNullNode = emptyNode.copy(outputEdgeNames = nonNullOutputs)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(null, Seq("", "")))
    val nullNode = emptyNode.copy(outputEdgeNames = nullOutputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeTypeNotNull" should "check for null in node type" in {
    val rule = new NodeTypeNotNull

    val nonNullNode = emptyNode
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = emptyNode.copy(className = null)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

}
