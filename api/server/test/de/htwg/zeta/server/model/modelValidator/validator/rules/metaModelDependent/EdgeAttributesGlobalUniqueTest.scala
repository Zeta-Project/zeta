package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType", sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeAttributesGlobalUnique("edgeType", "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attributeOne: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value")))
    val edgeOne = Edge("edge1Id", mReference, Set(), Set(), attributeOne)

    val attributeTwo: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("differentValue")))
    val edgeTwo = Edge("edge2Id", mReference, Set(), Set(), attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.forall(_.valid) should be(true)

    val attributeThree: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("anotherValue"), MString("yetAnotherValue")))
    val edgeThree = Edge("edge3Id", mReference, Set(), Set(), attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(_.valid) should be(true)

  }

  it should "return failure validation results on invalid attributes" in {
    val attributeOne: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("duplicateValue")))
    val edgeOne = Edge("edge1Id", mReference, Set(), Set(), attributeOne)

    val attributeTwo: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("duplicateValue")))
    val edgeTwo = Edge("edge2Id", mReference, Set(), Set(), attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.head.valid should be(false)
    results.last.valid should be(false)

    val attributeThree: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("duplicateValue"), MString("anotherValue")))
    val edgeThree = Edge("edge3Id", mReference, Set(), Set(), attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(!_.valid) should be(true)

    val attributeFour: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("differentValue")))
    val edgeFour = Edge("edge4Id", mReference, Set(), Set(), attributeFour)

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
      MString(""),
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
      MString(""),
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
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute](globalUniqueAttribute, nonGlobalUniqueAttribute)
    )
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeAttributesGlobalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributesGlobalUnique =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
      case _ => fail
    }
  }

}
