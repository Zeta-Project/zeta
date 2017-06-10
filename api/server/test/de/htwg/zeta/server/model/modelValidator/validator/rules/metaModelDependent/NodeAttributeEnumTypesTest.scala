package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
  val rule = new NodeAttributeEnumTypes("nodeType", "attributeType", "enumName")

  "isValid" should "be true for valid nodes" in {
    val mEnum = MEnum("enumName", Seq())
    val attribute = Attribute("attributeType", Seq(EnumSymbol("enumName", mEnum.name)))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be (true)
  }

  it should "be false for invalid nodes" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute = Attribute(name = "attributeType", value = Seq(EnumSymbol("differentEnumName", differentEnum.name)))
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))

    rule.isValid(node).get should be(false)
  }

  it should "be None for non-matching nodes" in {
    val differentClass = MClass("differentClass", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node.apply2("", differentClass, Seq(), Seq(), Seq())

    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inNodes "nodeType" areOfEnumType "enumName"""")
  }

}
