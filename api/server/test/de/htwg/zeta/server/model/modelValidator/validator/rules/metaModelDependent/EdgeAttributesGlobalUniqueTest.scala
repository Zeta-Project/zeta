package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType", sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeAttributesGlobalUnique("edgeType", "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attributeOne = Map("attributeType" -> Seq(MString("value")))
    val edgeOne = Edge("edge1Id", mReference, Seq(), Seq(), attributeOne)

    val attributeTwo = Map("attributeType" -> Seq(MString("differentValue")))
    val edgeTwo = Edge("edge2Id", mReference, Seq(), Seq(), attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.forall(_.valid) should be(true)

    val attributeThree = Map("attributeType" -> Seq(MString("anotherValue"), MString("yetAnotherValue")))
    val edgeThree = Edge("edge3Id", mReference, Seq(), Seq(), attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(_.valid) should be(true)

  }

  it should "return failure validation results on invalid attributes" in {
    val attributeOne = Map("attributeType" -> Seq(MString("duplicateValue")))
    val edgeOne = Edge("edge1Id", mReference, Seq(), Seq(), attributeOne)

    val attributeTwo = Map("attributeType" -> Seq(MString("duplicateValue")))
    val edgeTwo = Edge("edge2Id", mReference, Seq(), Seq(), attributeTwo)

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be(2)
    results.head.valid should be(false)
    results.last.valid should be(false)

    val attributeThree = Map("attributeType" -> Seq(MString("duplicateValue"), MString("anotherValue")))
    val edgeThree = Edge("edge3Id", mReference, Seq(), Seq(), attributeThree)

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be(3)
    secondResults.forall(!_.valid) should be(true)

    val attributeFour = Map("attributeType" -> Seq(MString("differentValue")))
    val edgeFour = Edge("edge4Id", mReference, Seq(), Seq(), attributeFour)

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

}
