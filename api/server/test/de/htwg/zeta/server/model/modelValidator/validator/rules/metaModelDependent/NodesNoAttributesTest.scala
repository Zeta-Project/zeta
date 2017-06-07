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

class NodesNoAttributesTest extends FlatSpec with Matchers {

  val rule = new NodesNoAttributes("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no attributes" in {
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq())
    rule.isValid(node).get should be (true)
  }

  it should "return false on nodes of type nodeType with attributes" in {
    val attribute = Attribute("attributeType", Seq(MString("")))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))
    rule.isValid(node).get should be (false)
  }

  it should "return true on nodes of type nodeType with empty attribute values" in {
    val attribute = Attribute("attributeType", Seq())
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))
    rule.isValid(node).get should be (true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Nodes ofType "nodeType" haveNoAttributes ()""")
  }

}
