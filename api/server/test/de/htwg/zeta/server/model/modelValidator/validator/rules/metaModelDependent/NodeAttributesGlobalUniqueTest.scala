package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mClass1 = MClass("nodeType1", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val mClass2 = MClass("nodeType2", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val rule = new NodeAttributesGlobalUnique(Seq("nodeType1", "nodeType2"), "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attribute1: Map[String, AttributeValue] = Map("attributeType" -> StringValue("value1"))
    val node1 = Node.empty("", mClass1.name, Seq(), Seq()).copy(attributeValues = attribute1)

    val attribute2: Map[String, AttributeValue] = Map("attributeType" -> StringValue("value2"))
    val node2 = Node.empty("", mClass1.name, Seq(), Seq()).copy(attributeValues = attribute2)
    val attribute3: Map[String, AttributeValue] = Map("attributeType" -> StringValue("value3"))
    val node3 = Node.empty("", mClass2.name, Seq(), Seq()).copy(attributeValues = attribute3)

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be(3)
    results.forall(_.valid) should be(true)
  }

  it should "return failure validation results on invalid attributes" in {
    val attribute1: Map[String, AttributeValue] = Map("attributeType" -> StringValue("duplicateValue"))
    val node1 = Node.empty("", mClass1.name, Seq(), Seq()).copy(attributeValues = attribute1)
    val attribute2: Map[String, AttributeValue] = Map("attributeType" -> StringValue("value"))
    val node2 = Node.empty("", mClass1.name, Seq(), Seq()).copy(attributeValues = attribute2)
    val attribute3: Map[String, AttributeValue] = Map("attributeType" -> StringValue("duplicateValue"))
    val node3 = Node.empty("", mClass2.name, Seq(), Seq()).copy(attributeValues = attribute3)

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be(3)
    results.head.valid should be(false)
    results(1).valid should be(true)
    results(2).valid should be(false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes Seq("nodeType1", "nodeType2") areGlobalUnique ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val globalUniqueAttribute = MAttribute("attributeName", globalUnique = true, localUnique = false, StringType, StringValue(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val nonGlobalUniqueAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]
      (nonGlobalUniqueAttribute, globalUniqueAttribute), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodeAttributesGlobalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributesGlobalUnique =>
        rule.nodeTypes should be(Seq("class"))
        rule.attributeType should be("attributeName")
      case _ => fail
    }

  }

}
