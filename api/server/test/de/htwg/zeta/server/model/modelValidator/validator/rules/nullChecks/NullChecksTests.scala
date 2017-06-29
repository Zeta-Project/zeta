package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import scala.collection.immutable.Seq

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

  def nodesToModel(nodes: Seq[Node]): Model = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), nodes, Seq.empty, "")

  def edgesToModel(edges: Seq[Edge]): Model = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), Seq.empty, edges, "")

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "EdgeAttributesNamesNotNull" should "check for null in edge attributes names" in {
    val rule = new EdgeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullEdge = Edge("", mReference, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null: String) -> Seq(), "name" -> Seq())
    val nullEdge = Edge("", mReference, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNoNullValues" should "check for null in edge attributes values" in {
    val rule = new EdgeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullEdge = Edge("", mReference, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), null)
    val nullEdge = Edge("", mReference, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNotNull" should "check for null edge attributes" in {
    val rule = new EdgeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullEdge = Edge("", mReference, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullEdge = Edge("", mReference, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNoNullValues" should "check for null in edge attribute value values" in {
    val rule = new EdgeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullEdge = Edge("", mReference, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(null))
    val nullEdge = Edge("", mReference, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new EdgeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullEdge = Edge("", mReference, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> null)
    val nullEdge = Edge("", mReference, Seq(), Seq(), nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNoNullValues" should "check for null in edge sources nodes values" in {
    val rule = new EdgeSourcesNodesNoNullValues

    val nonNullSources = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(ToNodes(mClass, Seq("node1", null)))
    val nullEdge = Edge("", mReference, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNodesNotNull" should "check for null in edge sources nodes" in {
    val rule = new EdgeSourcesNodesNotNull

    val nonNullSources = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(ToNodes(mClass, null))
    val nullEdge = Edge("", mReference, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNoNullValues" should "check for null in edge sources values" in {
    val rule = new EdgeSourcesNoNullValues

    val nonNullSources = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources: Seq[ToNodes] = Seq(null)
    val nullEdge = Edge("", mReference, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNotNull" should "check for null in edge sources" in {
    val rule = new EdgeSourcesNotNull

    val nonNullSources = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = null
    val nullEdge = Edge("", mReference, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesTypeNotNull" should "check for null in edge sources type" in {
    val rule = new EdgeSourcesTypeNotNull

    val nonNullSources = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, nonNullSources, Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullSources = Seq(ToNodes(null, Seq("node1", "node2")))
    val nullEdge = Edge("", mReference, nullSources, Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNoNullValues" should "check for null in edge targets nodes values" in {
    val rule = new EdgeTargetsNodesNoNullValues

    val nonNullTargets = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(ToNodes(mClass, Seq("node1", null)))
    val nullEdge = Edge("", mReference, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNodesNotNull" should "check for null in edge targets nodes" in {
    val rule = new EdgeTargetsNodesNotNull

    val nonNullTargets = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(ToNodes(mClass, null))
    val nullEdge = Edge("", mReference, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNoNullValues" should "check for null values in edge targets values" in {
    val rule = new EdgeTargetsNoNullValues

    val nonNullTargets = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets: Seq[ToNodes] = Seq(null)
    val nullEdge = Edge("", mReference, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNotNull" should "check for null in edge targets" in {
    val rule = new EdgeTargetsNotNull

    val nonNullTargets = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = null
    val nullEdge = Edge("", mReference, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsTypeNotNull" should "check for null in edge targets type" in {
    val rule = new EdgeTargetsTypeNotNull

    val nonNullTargets = Seq(ToNodes(mClass, Seq("node1", "node2")))
    val nonNullEdge = Edge("", mReference, Seq(), nonNullTargets, Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullTargets = Seq(ToNodes(null, Seq("node1", "node2")))
    val nullEdge = Edge("", mReference, Seq(), nullTargets, Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTypeNotNull" should "check for null in edge type" in {
    val rule = new EdgeTypeNotNull

    val nonNullEdge = Edge("", mReference, Seq(), Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge("", null, Seq(), Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsIdNotNull" should "check for null in elements id" in {
    val rule = new ElementsIdNotNull

    val nonNullEdge = Edge("", mReference, Seq(), Seq(), Map.empty)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = Edge(null, mReference, Seq(), Seq(), Map.empty)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNoNullValues" should "check for null in elements values" in {
    val rule = new ElementsNoNullValues

    val nonNullEdge = Edge("", mReference, Seq(), Seq(), Map.empty)
    val nonNullModel = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), Seq.empty, Seq(nonNullEdge), "")
    rule.check(nonNullModel) should be(true)

    val nullEdge = null
    val nullModel = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), Seq.empty, Seq(nullEdge), "")
    rule.check(nullModel) should be(false)
  }

  "ElementsNotNull" should "check for null in elements" in {
    val rule = new ElementsNotNull

    val nonNullModel = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), Seq.empty, Seq.empty, "")
    rule.check(nonNullModel) should be(true)

    val nullModel = Model("", MetaModel("", Seq.empty, Seq.empty, Seq.empty, ""), null, Seq.empty, "")
    rule.check(nullModel) should be(false)
  }

  // ------------------------------------------------

  "NodeAttributesNamesNotNull" should "check for null in nodes attributes names" in {
    val rule = new NodeAttributesNamesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(), "name2" -> Seq())
    val nonNullNode = Node("", mClass, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map((null: String) -> Seq(), "name" -> Seq())
    val nullNode = Node("", mClass, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNoNullValues" should "check for null in node attributes values" in {
    val rule = new NodeAttributesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullNode = Node("", mClass, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), null)
    val nullNode = Node("", mClass, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNotNull" should "check for null node attributes" in {
    val rule = new NodeAttributesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullNode = Node("", mClass, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullNode = Node("", mClass, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNoNullValues" should "check for null in node attribute value values" in {
    val rule = new NodeAttributesValuesNoNullValues

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullNode = Node("", mClass, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(null))
    val nullNode = Node("", mClass, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new NodeAttributesValuesNotNull

    val nonNullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> Seq(MInt(0)))
    val nonNullNode = Node("", mClass, Seq(), Seq(), nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, Seq[AttributeValue]] = Map("name1" -> Seq(MString("value1")), "name2" -> null)
    val nullNode = Node("", mClass, Seq(), Seq(), nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNoNullValues" should "check for null in node input edges values" in {
    val rule = new NodeInputsEdgesNoNullValues

    val nonNullInputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(ToEdges(mReference, Seq("edge1", null)))
    val nullNode = Node("", mClass, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsEdgesNotNull" should "check for null in node input edges" in {
    val rule = new NodeInputsEdgesNotNull

    val nonNullInputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(ToEdges(mReference, null))
    val nullNode = Node("", mClass, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNoNullValues" should "check for null in node input values" in {
    val rule = new NodeInputsNoNullValues

    val nonNullInputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs: Seq[ToEdges] = Seq(null)
    val nullNode = Node("", mClass, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNotNull" should "check for null in node inputs" in {
    val rule = new NodeInputsNotNull

    val nonNullInputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = null
    val nullNode = Node("", mClass, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsTypeNotNull" should "check for null in node inputs type" in {
    val rule = new NodeInputsTypeNotNull

    val nonNullInputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, Seq(), nonNullInputs, Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs = Seq(ToEdges(null, Seq("edge1", "edge2")))
    val nullNode = Node("", mClass, Seq(), nullInputs, Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNoNullValues" should "check for null in node output edges values" in {
    val rule = new NodeOutputsEdgesNoNullValues

    val nonNullOutputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(ToEdges(mReference, Seq("edge1", null)))
    val nullNode = Node("", mClass, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsEdgesNotNull" should "check for null in node output edges" in {
    val rule = new NodeOutputsEdgesNotNull

    val nonNullOutputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(ToEdges(mReference, null))
    val nullNode = Node("", mClass, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNoNullValues" should "check for null values in node output values" in {
    val rule = new NodeOutputsNoNullValues

    val nonNullOutputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs: Seq[ToEdges] = Seq(null)
    val nullNode = Node("", mClass, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNotNull" should "check for null in node outputs" in {
    val rule = new NodeOutputsNotNull

    val nonNullOutputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = null
    val nullNode = Node("", mClass, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsTypeNotNull" should "check for null in node outputs type" in {
    val rule = new NodeOutputsTypeNotNull

    val nonNullOutputs = Seq(ToEdges(mReference, Seq("edge1", "edge2")))
    val nonNullNode = Node("", mClass, nonNullOutputs, Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullOutputs = Seq(ToEdges(null, Seq("edge1", "edge2")))
    val nullNode = Node("", mClass, nullOutputs, Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeTypeNotNull" should "check for null in node type" in {
    val rule = new NodeTypeNotNull

    val nonNullNode = Node("", mClass, Seq(), Seq(), Map.empty)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = Node("", null, Seq(), Seq(), Map.empty)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

}
