package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodeAttributeEnumTypesTest extends AnyFlatSpec with Matchers {

  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: NodeInstance = NodeInstance.empty("", mClass.name, Seq.empty, Seq.empty)
  val rule = new NodeAttributeEnumTypes("nodeType", "attributeType", "enumName")

  "isValid" should "be true for valid nodes" in {
    val mEnum = MEnum("enumName", Seq())
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(EnumValue("enumName", mEnum.name)))
    val node = emptyNode.copy(attributeValues = attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val differentEnum = MEnum(name = "differentEnumName", valueNames = Seq())
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(EnumValue("differentEnumName", differentEnum.name)))
    val node = emptyNode.copy(attributeValues = attribute)

    rule.isValid(node).get should be(false)
  }

  it should "be None for non-matching nodes" in {
    val differentClass = MClass("differentClass", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = emptyNode.copy(className = differentClass.name)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfEnumType "enumName"""")
  }

  "generateFor" should "generate this rule from the meta model" ignore {
    val enum = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enum.typ, enum.values.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment =
      false, "", ordered = false, transient = false)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](enumAttribute, scalarAttribute),
      Seq())
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
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
