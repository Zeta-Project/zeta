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

class EdgeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

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
  val rule = new EdgesAttributesGlobalUnique("edgeType", "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attributeOne: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("value")))
    val edgeOne = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeOne)

    val attributeTwo: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("differentValue")))
    val edgeTwo = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.forall(_.valid) should be(true)

    val attributeThree: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("anotherValue"), StringValue("yetAnotherValue")))
    val edgeThree = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(_.valid) should be(true)

  }

  it should "return failure validation results on invalid attributes" in {
    val attributeOne: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("duplicateValue")))
    val edgeOne = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeOne)

    val attributeTwo: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("duplicateValue")))
    val edgeTwo = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.head.valid should be(false)
    results.last.valid should be(false)

    val attributeThree: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("duplicateValue"), StringValue("anotherValue")))
    val edgeThree = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(!_.valid) should be(true)

    val attributeFour: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("differentValue")))
    val edgeFour = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributeFour)

    val thirdResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree, edgeFour))

    thirdResults.size should be(4)
    thirdResults.head.valid should be(false)
    thirdResults(1).valid should be(false)
    thirdResults(2).valid should be(false)
    thirdResults(3).valid should be(true)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "edgeType" areGlobalUnique ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val globalUniqueAttribute = MAttribute(
      "attributeName",
      globalUnique = true,
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
    val nonGlobalUniqueAttribute = MAttribute(
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
      Seq[MAttribute](globalUniqueAttribute, nonGlobalUniqueAttribute),
      Seq.empty
    )
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgesAttributesGlobalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgesAttributesGlobalUnique =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
      case _ => fail
    }
  }

}
