package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NullChecksTests extends FlatSpec with Matchers {

  def nodesToModel(nodes: Set[Node]): Model = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), nodes, Set.empty, "")

  def edgesToModel(edges: Set[Edge]): Model = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), Set.empty, edges, "")

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set.empty)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "EdgeAttributesNamesNotNull" should "check for null in edge attributes names" in {
    val rule = new EdgeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(), "name2" -> Set())
    val nonNullEdge = Edge("", mReference, Set(), Set(), nonNullAttributes)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map((null: String) -> Set(), "name" -> Set())
    val nullEdge = Edge("", mReference, Set(), Set(), nullAttributes)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNoNullValues" should "check for null in edge attributes values" in {
    val rule = new EdgeAttributesNoNullValues

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullEdge = Edge("", mReference, Set(), Set(), nonNullAttributes)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), null)
    val nullEdge = Edge("", mReference, Set(), Set(), nullAttributes)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNotNull" should "check for null edge attributes" in {
    val rule = new EdgeAttributesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullEdge = Edge("", mReference, Set(), Set(), nonNullAttributes)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullEdge = Edge("", mReference, Set(), Set(), nullAttributes)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNoNullValues" should "check for null in edge attribute value values" in {
    val rule = new EdgeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullEdge = Edge("", mReference, Set(), Set(), nonNullAttributes)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(null))
    val nullEdge = Edge("", mReference, Set(), Set(), nullAttributes)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new EdgeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullEdge = Edge("", mReference, Set(), Set(), nonNullAttributes)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> null)
    val nullEdge = Edge("", mReference, Set(), Set(), nullAttributes)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNoNullValues" should "check for null in edge sources nodes values" in {
    val rule = new EdgeSourcesNodesNoNullValues

    val nonNullSources = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Set(ToNodes(mClass, Set("node1", null)))
    val nullEdge = Edge("", mReference, nullSources, Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNotNull" should "check for null in edge sources nodes" in {
    val rule = new EdgeSourcesNodesNotNull

    val nonNullSources = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Set(ToNodes(mClass, null))
    val nullEdge = Edge("", mReference, nullSources, Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNoNullValues" should "check for null in edge sources values" in {
    val rule = new EdgeSourcesNoNullValues

    val nonNullSources = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources: Set[ToNodes] = Set(null)
    val nullEdge = Edge("", mReference, nullSources, Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNotNull" should "check for null in edge sources" in {
    val rule = new EdgeSourcesNotNull

    val nonNullSources = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = null
    val nullEdge = Edge("", mReference, nullSources, Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesTypeNotNull" should "check for null in edge sources type" in {
    val rule = new EdgeSourcesTypeNotNull

    val nonNullSources = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Set(ToNodes(null, Set("node1", "node2")))
    val nullEdge = Edge("", mReference, nullSources, Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNoNullValues" should "check for null in edge targets nodes values" in {
    val rule = new EdgeTargetsNodesNoNullValues

    val nonNullTargets = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Set(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Set(ToNodes(mClass, Set("node1", null)))
    val nullEdge = Edge("", mReference, Set(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNotNull" should "check for null in edge targets nodes" in {
    val rule = new EdgeTargetsNodesNotNull

    val nonNullTargets = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Set(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Set(ToNodes(mClass, null))
    val nullEdge = Edge("", mReference, Set(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNoNullValues" should "check for null values in edge targets values" in {
    val rule = new EdgeTargetsNoNullValues

    val nonNullTargets = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Set(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets: Set[ToNodes] = Set(null)
    val nullEdge = Edge("", mReference, Set(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNotNull" should "check for null in edge targets" in {
    val rule = new EdgeTargetsNotNull

    val nonNullTargets = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Set(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = null
    val nullEdge = Edge("", mReference, Set(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsTypeNotNull" should "check for null in edge targets type" in {
    val rule = new EdgeTargetsTypeNotNull

    val nonNullTargets = Set(ToNodes(mClass, Set("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Set(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Set(ToNodes(null, Set("node1", "node2")))
    val nullEdge = Edge("", mReference, Set(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTypeNotNull" should "check for null in edge type" in {
    val rule = new EdgeTypeNotNull

    val nonNullEdge = Edge("", mReference, Set(), Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge("", null, Set(), Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsIdNotNull" should "check for null in elements id" in {
    val rule = new ElementsIdNotNull

    val nonNullEdge = Edge("", mReference, Set(), Set(), Map.empty)
    val nonNullModel = edgesToModel(Set(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge(null, mReference, Set(), Set(), Map.empty)
    val nullModel = edgesToModel(Set(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNoNullValues" should "check for null in elements values" in {
    val rule = new ElementsNoNullValues

    val nonNullEdge = Edge("", mReference, Set(), Set(), Map.empty)
    val nonNullModel = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), Set.empty, Set(nonNullEdge), "")
    rule.check(nonNullModel) should be(true)

    val nullEdge = null
    val nullModel = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), Set.empty, Set(nullEdge), "")
    rule.check(nullModel) should be(false)
  }

  "ElementsNotNull" should "check for null in elements" in {
    val rule = new ElementsNotNull

    val nonNullModel = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), Set.empty, Set.empty, "")
    rule.check(nonNullModel) should be(true)

    val nullModel = Model("", MetaModel("", Set.empty, Set.empty, Set.empty, ""), null, Set.empty, "")
    rule.check(nullModel) should be(false)
  }

  // ------------------------------------------------

  "NodeAttributesNamesNotNull" should "check for null in nodes attributes names" in {
    val rule = new NodeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(), "name2" -> Set())
    val nonNullNode = Node("", mClass, Set(), Set(), nonNullAttributes)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map((null: String) -> Set(), "name" -> Set())
    val nullNode = Node("", mClass, Set(), Set(), nullAttributes)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNoNullValues" should "check for null in node attributes values" in {
    val rule = new NodeAttributesNoNullValues

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullNode = Node("", mClass, Set(), Set(), nonNullAttributes)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), null)
    val nullNode = Node("", mClass, Set(), Set(), nullAttributes)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNotNull" should "check for null node attributes" in {
    val rule = new NodeAttributesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullNode = Node("", mClass, Set(), Set(), nonNullAttributes)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullNode = Node("", mClass, Set(), Set(), nullAttributes)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNoNullValues" should "check for null in node attribute value values" in {
    val rule = new NodeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullNode = Node("", mClass, Set(), Set(), nonNullAttributes)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(null))
    val nullNode = Node("", mClass, Set(), Set(), nullAttributes)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new NodeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> Set(MInt(0)))
    val nonNullNode = Node("", mClass, Set(), Set(), nonNullAttributes)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Set[AttributeValue]] = Map("name1" -> Set(MString("value1")), "name2" -> null)
    val nullNode = Node("", mClass, Set(), Set(), nullAttributes)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNoNullValues" should "check for null in node input edges values" in {
    val rule = new NodeInputsEdgesNoNullValues

    val nonNullInputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Set(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Set(ToEdges(mReference, Set("edge1", null)))
    val nullNode = Node("", mClass, Set(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNotNull" should "check for null in node input edges" in {
    val rule = new NodeInputsEdgesNotNull

    val nonNullInputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Set(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Set(ToEdges(mReference, null))
    val nullNode = Node("", mClass, Set(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNoNullValues" should "check for null in node input values" in {
    val rule = new NodeInputsNoNullValues

    val nonNullInputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Set(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs: Set[ToEdges] = Set(null)
    val nullNode = Node("", mClass, Set(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNotNull" should "check for null in node inputs" in {
    val rule = new NodeInputsNotNull

    val nonNullInputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Set(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = null
    val nullNode = Node("", mClass, Set(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsTypeNotNull" should "check for null in node inputs type" in {
    val rule = new NodeInputsTypeNotNull

    val nonNullInputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Set(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Set(ToEdges(null, Set("edge1", "edge2")))
    val nullNode = Node("", mClass, Set(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNoNullValues" should "check for null in node output edges values" in {
    val rule = new NodeOutputsEdgesNoNullValues

    val nonNullOutputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Set(ToEdges(mReference, Set("edge1", null)))
    val nullNode = Node("", mClass, nullOutputs, Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNotNull" should "check for null in node output edges" in {
    val rule = new NodeOutputsEdgesNotNull

    val nonNullOutputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Set(ToEdges(mReference, null))
    val nullNode = Node("", mClass, nullOutputs, Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNoNullValues" should "check for null values in node output values" in {
    val rule = new NodeOutputsNoNullValues

    val nonNullOutputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs: Set[ToEdges] = Set(null)
    val nullNode = Node("", mClass, nullOutputs, Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNotNull" should "check for null in node outputs" in {
    val rule = new NodeOutputsNotNull

    val nonNullOutputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = null
    val nullNode = Node("", mClass, nullOutputs, Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsTypeNotNull" should "check for null in node outputs type" in {
    val rule = new NodeOutputsTypeNotNull

    val nonNullOutputs = Set(ToEdges(mReference, Set("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Set(ToEdges(null, Set("edge1", "edge2")))
    val nullNode = Node("", mClass, nullOutputs, Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeTypeNotNull" should "check for null in node type" in {
    val rule = new NodeTypeNotNull

    val nonNullNode = Node("", mClass, Set(), Set(), Map.empty)
    val nonNullModel = nodesToModel(Set(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = Node("", null, Set(), Set(), Map.empty)
    val nullModel = nodesToModel(Set(nullNode))
    rule.check(nullModel) should be(false)
  }

}
