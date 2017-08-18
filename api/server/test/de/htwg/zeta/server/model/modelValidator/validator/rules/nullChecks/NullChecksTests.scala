package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NullChecksTests extends FlatSpec with Matchers {

  def nodesToModel(nodes: Seq[Node]): Model = Model("", UUID.randomUUID(), nodes, Seq.empty, Map.empty, "")

  def edgesToModel(edges: Seq[Edge]): Model = Model("", UUID.randomUUID(), Seq.empty, edges, Map.empty, "")

  val mReference = MReference("edgeType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty,
    Seq.empty)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "EdgeAttributesNamesNotNull" should "check for null in edge attributes names" in {
    val rule = new EdgeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null, Seq()), "name" -> Seq())
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNoNullValues" should "check for null in edge attributes values" in {
    val rule = new EdgeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNotNull" should "check for null edge attributes" in {
    val rule = new EdgeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNoNullValues" should "check for null in edge attribute value values" in {
    val rule = new EdgeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(null))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new EdgeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNoNullValues" should "check for null in edge sources nodes values" in {
    val rule = new EdgeSourcesNodesNoNullValues

    val nonNullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), null)))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNotNull" should "check for null in edge sources nodes" in {
    val rule = new EdgeSourcesNodesNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(mClass.name, null))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNoNullValues" should "check for null in edge sources values" in {
    val rule = new EdgeSourcesNoNullValues

    val nonNullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources: Seq[NodeLink] = Seq(null)
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNotNull" should "check for null in edge sources" in {
    val rule = new EdgeSourcesNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = null
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesTypeNotNull" should "check for null in edge sources type" in {
    val rule = new EdgeSourcesTypeNotNull

    val nonNullSources = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(NodeLink(null, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNoNullValues" should "check for null in edge targets nodes values" in {
    val rule = new EdgeTargetsNodesNoNullValues

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), null)))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNotNull" should "check for null in edge targets nodes" in {
    val rule = new EdgeTargetsNodesNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(mClass.name, null))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNoNullValues" should "check for null values in edge targets values" in {
    val rule = new EdgeTargetsNoNullValues

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets: Seq[NodeLink] = Seq(null)
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNotNull" should "check for null in edge targets" in {
    val rule = new EdgeTargetsNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = null
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsTypeNotNull" should "check for null in edge targets type" in {
    val rule = new EdgeTargetsTypeNotNull

    val nonNullTargets = Seq(NodeLink(mClass.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(NodeLink(null, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTypeNotNull" should "check for null in edge type" in {
    val rule = new EdgeTypeNotNull

    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge(UUID.randomUUID(), null, Seq(), Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsIdNotNull" should "check for null in elements id" in {
    val rule = new ElementsIdNotNull

    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge(null, mReference.name, Seq(), Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNoNullValues" should "check for null in elements values" in {
    val rule = new ElementsNoNullValues

    val nonNullEdge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty)
    val nonNullModel = Model("", UUID.randomUUID(), Seq.empty, Seq(nonNullEdge), Map.empty, "")
    rule.check(nonNullModel) should be(true)

    val nullEdge = null
    val nullModel = Model("", UUID.randomUUID(), Seq.empty, Seq(nullEdge), Map.empty, "")
    rule.check(nullModel) should be(false)
  }

  "ElementsNotNull" should "check for null in elements" in {
    val rule = new ElementsNotNull

    val nonNullModel = Model("", UUID.randomUUID(), Seq.empty, Seq.empty, Map.empty, "")
    rule.check(nonNullModel) should be(true)

    val nullModel = Model("", UUID.randomUUID(), null, Seq.empty, Map.empty, "")
    rule.check(nullModel) should be(false)
  }

  // ------------------------------------------------

  "NodeAttributesNamesNotNull" should "check for null in nodes attributes names" in {
    val rule = new NodeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null: String) -> Seq(), "name" -> Seq())
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNoNullValues" should "check for null in node attributes values" in {
    val rule = new NodeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNotNull" should "check for null node attributes" in {
    val rule = new NodeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNoNullValues" should "check for null in node attribute value values" in {
    val rule = new NodeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(null))
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new NodeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> Seq(IntValue(0)))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(StringValue("value1")), "name2" -> null)
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNoNullValues" should "check for null in node input edges values" in {
    val rule = new NodeInputsEdgesNoNullValues

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), null)))
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNotNull" should "check for null in node input edges" in {
    val rule = new NodeInputsEdgesNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(mReference.name, null))
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNoNullValues" should "check for null in node input values" in {
    val rule = new NodeInputsNoNullValues

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs: Seq[EdgeLink] = Seq(null)
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNotNull" should "check for null in node inputs" in {
    val rule = new NodeInputsNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = null
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsTypeNotNull" should "check for null in node inputs type" in {
    val rule = new NodeInputsTypeNotNull

    val nonNullInputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(EdgeLink(null, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nullNode = Node(UUID.randomUUID(), mClass.name, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNoNullValues" should "check for null in node output edges values" in {
    val rule = new NodeOutputsEdgesNoNullValues

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), null)))
    val nullNode = Node(UUID.randomUUID(), mClass.name, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNotNull" should "check for null in node output edges" in {
    val rule = new NodeOutputsEdgesNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(mReference.name, null))
    val nullNode = Node(UUID.randomUUID(), mClass.name, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNoNullValues" should "check for null values in node output values" in {
    val rule = new NodeOutputsNoNullValues

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs: Seq[EdgeLink] = Seq(null)
    val nullNode = Node(UUID.randomUUID(), mClass.name, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNotNull" should "check for null in node outputs" in {
    val rule = new NodeOutputsNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = null
    val nullNode = Node(UUID.randomUUID(), mClass.name, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsTypeNotNull" should "check for null in node outputs type" in {
    val rule = new NodeOutputsTypeNotNull

    val nonNullOutputs = Seq(EdgeLink(mReference.name, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nonNullNode = Node(UUID.randomUUID(), mClass.name, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(EdgeLink(null, Seq(UUID.randomUUID(), UUID.randomUUID())))
    val nullNode = Node(UUID.randomUUID(), mClass.name, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeTypeNotNull" should "check for null in node type" in {
    val rule = new NodeTypeNotNull

    val nonNullNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = Node(UUID.randomUUID(), null, Seq(), Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

}
