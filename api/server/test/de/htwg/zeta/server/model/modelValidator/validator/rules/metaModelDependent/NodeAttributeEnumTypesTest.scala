package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new NodeAttributeEnumTypes("nodeType", "attributeType", "enumName")

  "isValid" should "be true for valid nodes" in {
    val mEnum = MEnum("enumName", Seq())
    val attribute = Attribute("attributeType", Seq(new EnumSymbol("enumName", mEnum)))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (true)
  }

  it should "be false for invalid nodes" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute = Attribute(name = "attributeType", value = Seq(new EnumSymbol("differentEnumName", differentEnum)))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be(false)
  }

  it should "be None for non-matching nodes" in {
    val differentClass = MClass("differentClass", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inNodes "nodeType" areOfEnumType "enumName"""")
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
    val result = NodeAttributeEnumTypes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributeEnumTypes =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName")
        rule.enumName should be ("enumName")
      case _ => fail
    }

  }

}
