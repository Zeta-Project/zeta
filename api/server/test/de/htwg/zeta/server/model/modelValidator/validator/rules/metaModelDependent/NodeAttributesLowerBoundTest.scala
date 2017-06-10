package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesLowerBound("nodeType", "attributeType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes with 2 or more attributes of type attributeType" in {
    val twoAttributes = Attribute("attributeType", Seq(MString("att1"), MString("att2")))
    val twoAttributeNode = Node.apply2("", mClass, Seq(), Seq(), Seq(twoAttributes))

    rule.isValid(twoAttributeNode).get should be(true)

    val threeAttributes = Attribute("attributeType", Seq(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesNode = Node.apply2("", mClass, Seq(), Seq(), Seq(threeAttributes))

    rule.isValid(threeAttributesNode).get should be(true)
  }

  it should "return false on nodes with less than 2 attributes of type attributeType" in {
    val noAttributes = Attribute("attributeType", Seq())
    val noAttributesNode = Node.apply2("", mClass, Seq(), Seq(), Seq(noAttributes))

    rule.isValid(noAttributesNode).get should be(false)

    val oneAttribute = Attribute("attributeType", Seq(MString("att")))
    val oneAttributeNode = Node.apply2("", mClass, Seq(), Seq(), Seq(oneAttribute))

    rule.isValid(oneAttributeNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveLowerBound 2""")
  }

}
