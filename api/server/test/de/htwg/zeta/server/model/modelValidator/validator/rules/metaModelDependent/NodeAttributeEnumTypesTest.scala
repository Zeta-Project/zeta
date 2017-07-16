package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val rule = new NodeAttributeEnumTypes("nodeType", "attributeType", "enumName")

  "isValid" should "be true for valid nodes" in {
    val mEnum = MEnum("enumName", Seq())
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(EnumSymbol("enumName", mEnum.name)))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(EnumSymbol("differentEnumName", differentEnum.name)))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "be None for non-matching nodes" in {
    val differentClass = MClass("differentClass", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = Node("", differentClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfEnumType "enumName"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumType.symbols.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false, singleAssignment =
      false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](enumAttribute, scalarAttribute),
      Seq())
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeAttributeEnumTypes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributeEnumTypes =>
        rule.nodeType should be("class")
        rule.attributeType should be("attributeName")
        rule.enumName should be("enumName")
      case _ => fail
    }

  }

}
