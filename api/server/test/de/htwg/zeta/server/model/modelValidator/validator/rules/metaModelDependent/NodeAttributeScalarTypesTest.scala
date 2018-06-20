package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeScalarTypesTest extends FlatSpec with Matchers {
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: NodeInstance = NodeInstance.empty("", mClass.name, Seq.empty, Seq.empty)
  val rule = new NodeAttributeScalarTypes("nodeType", "attributeType", StringType)

  "isValid" should "be true for valid nodes" in {
    val node = emptyNode.copy(attributeValues = Map("attributeType" -> List(StringValue(""))))
    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val node = emptyNode.copy(attributeValues = Map("attributeType" -> List(IntValue(0))))
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct String" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enum = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enum.typ, enum.values.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment =
      false, "", ordered = false, transient = false)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](enumAttribute,
      scalarAttribute), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
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
