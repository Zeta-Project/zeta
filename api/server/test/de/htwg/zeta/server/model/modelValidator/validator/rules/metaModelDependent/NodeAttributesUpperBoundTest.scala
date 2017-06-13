package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesUpperBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesUpperBound("nodeType", "attributeType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes with 2 or less attributes of type attributeType" in {
    val noAttributes = Map("attributeType" -> Seq())
    val noAttributesNode = Node("", mClass, Seq(), Seq(), noAttributes)

    rule.isValid(noAttributesNode).get should be(true)

    val oneAttribute = Map("attributeType" -> Seq(MString("att")))
    val oneAttributeNode = Node("", mClass, Seq(), Seq(), oneAttribute)

    rule.isValid(oneAttributeNode).get should be(true)

    val twoAttributes = Map("attributeType" -> Seq(MString("att1"), MString("att2")))
    val twoAttributesNode = Node("", mClass, Seq(), Seq(), twoAttributes)

    rule.isValid(twoAttributesNode).get should be(true)
  }

  it should "return false on nodes with more than 2 attributes of type attributeType" in {
    val threeAttributes = Map("attributeType" -> Seq(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesNode = Node("", mClass, Seq(), Seq(), threeAttributes)

    rule.isValid(threeAttributesNode).get should be(false)

    val fourAttributes = Map("attributeType" -> Seq(MString("att1"), MString("att2"), MString("att3"), MString("att4")))
    val fourAttributesNode = Node("", mClass, Seq(), Seq(), fourAttributes)
    rule.isValid(fourAttributesNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveUpperBound 2""")
  }

}
