package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new NodeAttributes("nodeType", Seq("att1", "att2"))

  "isValid" should "return true for valid nodes" in {
    val attributes = Seq(
      Attribute("att1", Seq(MString(""))),
      Attribute("att2", Seq(MBool(false)))
    )
    val node = Node.apply2("", mClass, Seq(), Seq(), attributes)

    rule.isValid(node).get should be (true)
  }

  it should "return false for invalid nodes" in {
    val attributes = Seq(
      Attribute("att1", Seq(MString(""))),
      Attribute("att2", Seq(MBool(false))),
      Attribute("att3", Seq(MInt(0)))
    )
    val node = Node.apply2("", mClass, Seq(), Seq(), attributes)

    rule.isValid(node).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes inNodes "nodeType" areOfTypes Seq("att1", "att2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute1 = MAttribute("attributeName1", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](attribute1, attribute2))
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeAttributes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributes =>
        rule.nodeType should be ("class")
        rule.attributeTypes should be (Seq("attributeName1", "attributeName2"))
      case _ => fail
    }

  }

}
