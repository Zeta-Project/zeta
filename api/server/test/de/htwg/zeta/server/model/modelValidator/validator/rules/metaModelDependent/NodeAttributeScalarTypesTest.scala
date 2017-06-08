package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeScalarTypesTest extends FlatSpec with Matchers {
  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new NodeAttributeScalarTypes("nodeType", "attributeType", ScalarType.String)

  "isValid" should "be true for valid nodes" in {
    val attribute = Attribute("attributeType", Seq(MString("")))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (true)
  }

  it should "be false for invalid nodes" in {
    val attribute = Attribute("attributeType", Seq(MInt(0)))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct String" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inNodes "nodeType" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val tmpEnumType = MEnum("enumName", Seq())

    val enumValues = Seq(
      EnumSymbol("enumValue1", tmpEnumType),
      EnumSymbol("enumValue2", tmpEnumType)
    )

    val enumType = tmpEnumType.copy(values = enumValues)
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumValues.head, constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](enumAttribute, scalarAttribute))
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeAttributeScalarTypes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributeScalarTypes =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName2")
        rule.attributeDataType should be (ScalarType.String)
      case _ => fail
    }

  }
}
