package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeScalarTypesTest extends FlatSpec with Matchers {
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val rule = new NodeAttributeScalarTypes("nodeType", "attributeType", StringType)

  "isValid" should "be true for valid nodes" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("")))
    val node = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(IntValue(0)))
    val node = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = Node(UUID.randomUUID(), differentMClass.name, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct String" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumType.values.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment =
      false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](enumAttribute,
      scalarAttribute), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
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
