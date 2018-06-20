package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new EdgesAttributesGlobalUnique("edgeType", "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attributeOne: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("value")))
    val edgeOne = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeOne)

    val attributeTwo: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("differentValue")))
    val edgeTwo = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.forall(_.valid) should be(true)

    val attributeThree: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("anotherValue")))
    val edgeThree = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(_.valid) should be(true)

  }

  it should "return failure validation results on invalid attributes" in {
    val attributeOne: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("duplicateValue")))
    val edgeOne = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeOne)

    val attributeTwo: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("duplicateValue")))
    val edgeTwo = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.head.valid should be(false)
    results.last.valid should be(false)

    val attributeThree: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("duplicateValue")))
    val edgeThree = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(!_.valid) should be(true)

    val attributeFour: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("differentValue")))
    val edgeFour = EdgeInstance.empty("", mReference.name, "", "").copy(attributeValues = attributeFour)

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
      transient = false
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
      transient = false
    )
    val reference = MReference(
      "reference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      "",
      "",
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](globalUniqueAttribute, nonGlobalUniqueAttribute),
      Seq.empty
    )
    val metaModel = Concept.empty.copy(references = Seq(reference))
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
