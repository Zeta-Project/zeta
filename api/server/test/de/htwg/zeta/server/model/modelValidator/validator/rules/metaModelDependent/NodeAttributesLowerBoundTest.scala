package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesLowerBound("nodeType", "attributeType", 2)
  val mClass: MClass = MClass.empty("nodeType")
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes with 2 or more attributes of type attributeType" in {
    val twoAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2")))
    val twoAttributeNode = emptyNode.copy(attributeValues = twoAttributes)

    rule.isValid(twoAttributeNode).get should be(true)

    val threeAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2"), StringValue("att3")))
    val threeAttributesNode = emptyNode.copy(attributeValues = threeAttributes)

    rule.isValid(threeAttributesNode).get should be(true)
  }

  it should "return false on nodes with less than 2 attributes of type attributeType" in {
    val noAttributesNode = emptyNode.copy(attributeValues = Map("attributeType" -> Seq()))

    rule.isValid(noAttributesNode).get should be(false)

    val oneAttribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att")))
    val oneAttributeNode = emptyNode.copy(attributeValues = oneAttribute)

    rule.isValid(oneAttributeNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 5)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](attribute), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeAttributesLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributesLowerBound =>
        rule.nodeType should be("class")
        rule.attributeType should be("attributeName")
        rule.lowerBound should be(5)
      case _ => fail
    }

  }

}
