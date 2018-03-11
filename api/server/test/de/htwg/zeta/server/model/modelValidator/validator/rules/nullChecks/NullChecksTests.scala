package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.Node
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

// scalastyle:off null
class NullChecksTests extends FlatSpec with Matchers {

  def nodesToModel(nodes: Seq[NodeInstance]): GraphicalDslInstance = GraphicalDslInstance.empty("", UUID.randomUUID()).copy(nodes = nodes)

  def edgesToModel(edges: Seq[EdgeInstance]): GraphicalDslInstance = GraphicalDslInstance.empty("", UUID.randomUUID()).copy(edges = edges)

  val mReference: MReference = MReference.empty("edgeType", "", "")
  val mClass: MClass = MClass.empty("nodeType")

  val emptyNode: NodeInstance = NodeInstance.empty("", mClass.name, Seq.empty, Seq.empty)

  val emptyEdge: EdgeInstance = EdgeInstance.empty("", mReference.name, "", "")

  "EdgeAttributesNamesNotNull" should "check for null in edge attributes names" in {
    val rule = new EdgeAttributesNamesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue(""), "name2" -> StringValue(""))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map((null, StringValue("")), "name" -> StringValue(""))
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNoNullValues" should "check for null in edge attributes values" in {
    val rule = new EdgeAttributesNoNullValues

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> null)
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesNotNull" should "check for null edge attributes" in {
    val rule = new EdgeAttributesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null // scalastyle:ignore null
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new EdgeAttributesValuesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullEdge = emptyEdge.copy(attributeValues = nonNullAttributes)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> null)
    val nullEdge = emptyEdge.copy(attributeValues = nullAttributes)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeSourcesNotNull" should "check for null in edge sources" in {
    val rule = new EdgeSourcesNotNull

    val nonNullEdge = emptyEdge.copy(sourceNodeName = mClass.name)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = emptyEdge.copy(sourceNodeName = null)
    val nullModel = edgesToModel(Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "EdgeTargetsNotNull" should "check for null in edge targets" in {
    val rule = new EdgeTargetsNotNull

    val nonNullEdge = emptyEdge.copy(targetNodeName = mClass.name)
    val nonNullModel = edgesToModel(Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = emptyEdge.copy(targetNodeName = null)
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
    val nonNullModel = GraphicalDslInstance.empty("", UUID.randomUUID()).copy(edges = Seq(nonNullEdge))
    rule.check(nonNullModel) should be(true)

    val nullEdge = null
    val nullModel = GraphicalDslInstance.empty("", UUID.randomUUID()).copy(edges = Seq(nullEdge))
    rule.check(nullModel) should be(false)
  }

  "ElementsNotNull" should "check for null in elements" in {
    val rule = new ElementsNotNull

    val nonNullModel = GraphicalDslInstance.empty("", UUID.randomUUID())
    rule.check(nonNullModel) should be(true)

    val nullModel = GraphicalDslInstance.empty("", UUID.randomUUID()).copy(nodes = null)
    rule.check(nullModel) should be(false)
  }

  // ------------------------------------------------

  "NodeAttributesNamesNotNull" should "check for null in nodes attributes names" in {
    val rule = new NodeAttributesNamesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue(""), "name2" -> StringValue(""))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map((null: String) -> StringValue(""), "name" -> StringValue(""))
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNoNullValues" should "check for null in node attributes values" in {
    val rule = new NodeAttributesNoNullValues

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> null)
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesNotNull" should "check for null node attributes" in {
    val rule = new NodeAttributesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes = null
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeAttributesValuesNotNull" should "check for null in edge attribute values" in {
    val rule = new NodeAttributesValuesNotNull

    val nonNullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> IntValue(0))
    val nonNullNode = emptyNode.copy(attributeValues = nonNullAttributes)
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullAttributes: Map[String, AttributeValue] = Map("name1" -> StringValue("value1"), "name2" -> null)
    val nullNode = emptyNode.copy(attributeValues = nullAttributes)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNoNullValues" should "check for null in node input values" in {
    val rule = new NodeInputsNoNullValues

    val nonNullNode = emptyNode.copy(inputEdgeNames = Seq(mReference.name))
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullInputs: Seq[String] = Seq(null)
    val nullNode = emptyNode.copy(inputEdgeNames = nullInputs)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeInputsNotNull" should "check for null in node inputs" in {
    val rule = new NodeInputsNotNull

    val nonNullNode = emptyNode.copy(inputEdgeNames = Seq(mReference.name))
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = emptyNode.copy(inputEdgeNames = null)
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNoNullValues" should "check for null values in node output values" in {
    val rule = new NodeOutputsNoNullValues

    val nonNullNode = emptyNode.copy(outputEdgeNames = Seq(mReference.name))
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = emptyNode.copy(outputEdgeNames = Seq(null))
    val nullModel = nodesToModel(Seq(nullNode))
    rule.check(nullModel) should be(false)
  }

  "NodeOutputsNotNull" should "check for null in node outputs" in {
    val rule = new NodeOutputsNotNull

    val nonNullNode = emptyNode.copy(outputEdgeNames = Seq(mReference.name))
    val nonNullModel = nodesToModel(Seq(nonNullNode))
    rule.check(nonNullModel) should be(true)

    val nullNode = emptyNode.copy(outputEdgeNames = null)
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
