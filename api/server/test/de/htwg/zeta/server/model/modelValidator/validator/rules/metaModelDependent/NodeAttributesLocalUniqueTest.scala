package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLocalUniqueTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new NodeAttributesLocalUnique("nodeType", "attributeType")

  "isValid" should "return true on valid nodes" in {
    val attribute = Attribute("attributeType", Seq(MString("value1"), MString("value2"), MString("value3")))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (true)
  }

  it should "return false on invalid nodes" in {
    val attribute = Attribute("attributeType", Seq(MString("duplicateValue"), MString("value"), MString("duplicateValue")))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (false)
  }

  it should "return None for non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inNodes "nodeType" areLocalUnique ()""")
  }
}
