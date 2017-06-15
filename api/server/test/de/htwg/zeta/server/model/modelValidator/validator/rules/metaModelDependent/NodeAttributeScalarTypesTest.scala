package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeType.StringType
import models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeScalarTypesTest extends FlatSpec with Matchers {
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
  val rule = new NodeAttributeScalarTypes("nodeType", "attributeType", StringType)

  "isValid" should "be true for valid nodes" in {
    val attribute = Map("attributeType" -> Seq(MString("")))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val attribute = Map("attributeType" -> Seq(MInt(0)))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct String" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfScalarType "String"""")
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
