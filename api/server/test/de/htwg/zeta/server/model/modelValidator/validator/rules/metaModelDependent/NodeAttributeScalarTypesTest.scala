package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeScalarTypesTest extends FlatSpec with Matchers {
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
  val rule = new NodeAttributeScalarTypes("nodeType", "attributeType", StringType)

  "isValid" should "be true for valid nodes" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("")))
    val node = Node("", mClass, Set(), Set(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MInt(0)))
    val node = Node("", mClass, Set(), Set(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct String" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Set("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumType.symbols.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false, singleAssignment =
      false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute](enumAttribute,
      scalarAttribute))
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeAttributeScalarTypes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributeScalarTypes =>
        rule.nodeType should be("class")
        rule.attributeType should be("attributeName2")
        rule.attributeDataType should be(StringType)
      case _ => fail
    }

  }
}
