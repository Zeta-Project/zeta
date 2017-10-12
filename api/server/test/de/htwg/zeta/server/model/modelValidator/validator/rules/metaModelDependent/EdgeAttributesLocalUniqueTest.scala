package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesLocalUniqueTest extends FlatSpec with Matchers {

  val rule = new EdgeAttributesLocalUnique("edgeType", "attributeType")
  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )

  "isValid" should "return true on valid edges" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("valueOne"), StringValue("valueTwo"), StringValue("valueThree")))
    val edge = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attribute)

    rule.isValid(edge).get should be(true)
  }

  it should "return false on invalid edges" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("dupValue"), StringValue("dupValue"), StringValue("valueThree")))
    val edge = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val mReference = MReference(
      "differentEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = Edge.empty("", mReference.name, Seq(), Seq())

    rule.isValid(edge) should be(None)
  }

  "generateFor" should "generate this rule from the meta model" in {
    val localUniqueAttribute = MAttribute(
      "attributeName",
      globalUnique = false,
      localUnique = true,
      StringType,
      StringValue(""),
      constant = false,
      singleAssignment = false,
      "",
      ordered = false,
      transient = false,
      -1,
      0
    )
    val nonLocalUniqueAttribute = MAttribute(
      "attributeName2",
      globalUnique = false,
      localUnique = false,
      StringType,
      StringValue(""),
      constant = false,
      singleAssignment = false,
      "",
      ordered = false,
      transient = false,
      -1,
      0
    )
    val reference = MReference(
      "reference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](localUniqueAttribute, nonLocalUniqueAttribute),
      Seq.empty
    )
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeAttributesLocalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributesLocalUnique =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
      case _ => fail
    }
  }

}
