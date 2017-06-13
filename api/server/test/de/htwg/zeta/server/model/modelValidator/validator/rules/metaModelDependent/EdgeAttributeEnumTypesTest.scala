package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty, Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeAttributeEnumTypes("reference", "attributeType", "enumName")

  "the rule" should "be true for valid edges" in {
    val mEnum = MEnum(name = "enumName", values = Seq())
    val attribute = Map("attributeType" -> Seq(EnumSymbol("enumName", mEnum.name)))
    val edge = Edge("edgeId", mReference, Seq(), Seq(), attribute)

    rule.isValid(edge).get should be(true)
  }

  it should "return None for non-matching edge" in {
    val differentMReference = MReference(
      "differentMReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("edgeId", differentMReference, Seq(), Seq(), Map.empty)

    rule.isValid(edge) should be(None)
  }

  it should "be false for invalid edges" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute = Map("attributeType" -> Seq(EnumSymbol("differentEnumName", differentEnum.name)))
    val edge = Edge("edgeId", mReference, Seq(), Seq(), attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {
    val differentReference = MReference(
      "differentRef",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("", differentReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfEnumType "enumName"""")
  }

}
