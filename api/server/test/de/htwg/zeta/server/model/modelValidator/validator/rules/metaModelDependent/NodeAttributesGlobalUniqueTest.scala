package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mClass1 = MClass("nodeType1", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val mClass2 = MClass("nodeType2", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new NodeAttributesGlobalUnique(Seq("nodeType1", "nodeType2"), "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attribute1 = Attribute("attributeType", Seq(MString("value1")))
    val node1 = Node.apply2("", mClass1, Seq(), Seq(), Seq(attribute1))

    val attribute2 = Attribute("attributeType", Seq(MString("value2")))
    val node2 = Node.apply2("", mClass1, Seq(), Seq(), Seq(attribute2))

    val attribute3 = Attribute("attributeType", Seq(MString("value3")))
    val node3 = Node.apply2("", mClass2, Seq(), Seq(), Seq(attribute3))

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be (3)
    results.forall(_.valid) should be (true)
  }

  it should "return failure validation results on invalid attributes" in {
    val attribute1 = Attribute("attributeType", Seq(MString("duplicateValue")))
    val node1 = Node.apply2("", mClass1, Seq(), Seq(), Seq(attribute1))

    val attribute2 = Attribute("attributeType", Seq(MString("value")))
    val node2 = Node.apply2("", mClass1, Seq(), Seq(), Seq(attribute2))

    val attribute3 = Attribute("attributeType", Seq(MString("duplicateValue")))
    val node3 = Node.apply2("", mClass2, Seq(), Seq(), Seq(attribute3))

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be (3)
    results.head.valid should be (false)
    results(1).valid should be (true)
    results(2).valid should be (false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inNodes Seq("nodeType1", "nodeType2") areGlobalUnique ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val globalUniqueAttribute = MAttribute("attributeName", globalUnique = true, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val nonGlobalUniqueAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](nonGlobalUniqueAttribute, globalUniqueAttribute))
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeAttributesGlobalUnique.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributesGlobalUnique =>
        rule.nodeTypes should be (Seq("class"))
        rule.attributeType should be ("attributeName")
      case _ => fail
    }

  }

}
