package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoAttributesTest extends FlatSpec with Matchers {

  val rule = new NodesNoAttributes("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no attributes" in {
    val node = Node("", mClass, Seq(), Seq(), Map.empty)
    rule.isValid(node).get should be (true)
  }

  it should "return false on nodes of type nodeType with attributes" in {
    val attribute = Map("attributeType" -> Seq(MString("")))
    val node = Node("", mClass, Seq(), Seq(), attribute)
    rule.isValid(node).get should be (false)
  }

  it should "return true on nodes of type nodeType with empty attribute values" in {
    val attribute = Map("attributeType" -> Seq())
    val node = Node("", mClass, Seq(), Seq(), attribute)
    rule.isValid(node).get should be (true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Nodes ofType "nodeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodesNoAttributes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodesNoAttributes =>
        rule.nodeType should be ("class")
      case _ => fail
    }

  }

}
