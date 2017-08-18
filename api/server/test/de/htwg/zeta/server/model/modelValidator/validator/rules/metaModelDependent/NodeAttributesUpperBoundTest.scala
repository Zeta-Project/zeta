package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesUpperBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesUpperBound("nodeType", "attributeType", 2)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "isValid" should "return true on nodes with 2 or less attributes of type attributeType" in {
    val noAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq())
    val noAttributesNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), noAttributes)

    rule.isValid(noAttributesNode).get should be(true)

    val oneAttribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att")))
    val oneAttributeNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), oneAttribute)

    rule.isValid(oneAttributeNode).get should be(true)

    val twoAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2")))
    val twoAttributesNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), twoAttributes)

    rule.isValid(twoAttributesNode).get should be(true)
  }

  it should "return false on nodes with more than 2 attributes of type attributeType" in {
    val threeAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2"), StringValue("att3")))
    val threeAttributesNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), threeAttributes)

    rule.isValid(threeAttributesNode).get should be(false)

    val fourAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2"), StringValue("att3"), StringValue("att4")))
    val fourAttributesNode = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), fourAttributes)
    rule.isValid(fourAttributesNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = Node(UUID.randomUUID(), differentMClass.name, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, 7, 0)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](attribute), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeAttributesUpperBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributesUpperBound =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName")
        rule.upperBound should be (7)
      case _ => fail
    }

  }

}
