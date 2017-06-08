package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.ScalarType
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

  "generateFor" should "generate this rule from the meta model" in {
    val localUniqueAttribute = MAttribute("attributeName", globalUnique = false, localUnique = true, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val nonLocalUniqueAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](nonLocalUniqueAttribute, localUniqueAttribute))
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeAttributesLocalUnique.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributesLocalUnique =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName")
      case _ => fail
    }

  }
}
