package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
  val rule = new NodeAttributeEnumTypes("nodeType", "attributeType", "enumName")

  "isValid" should "be true for valid nodes" in {
    val mEnum = MEnum("enumName", Seq())
    val attribute = Map("attributeType" -> Seq(EnumSymbol("enumName", mEnum.name)))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "be false for invalid nodes" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute = Map("attributeType" -> Seq(EnumSymbol("differentEnumName", differentEnum.name)))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "be None for non-matching nodes" in {
    val differentClass = MClass("differentClass", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areOfEnumType "enumName"""")
  }

}
