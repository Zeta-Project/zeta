package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLocalUniqueTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
  val rule = new NodeAttributesLocalUnique("nodeType", "attributeType")

  "isValid" should "return true on valid nodes" in {
    val attribute = Map("attributeType" -> Seq(MString("value1"), MString("value2"), MString("value3")))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "return false on invalid nodes" in {
    val attribute = Map("attributeType" -> Seq(MString("duplicateValue"), MString("value"), MString("duplicateValue")))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "return None for non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areLocalUnique ()""")
  }
}
