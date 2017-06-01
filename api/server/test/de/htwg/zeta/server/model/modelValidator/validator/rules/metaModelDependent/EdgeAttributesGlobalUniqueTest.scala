package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributesGlobalUnique("edgeType", "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attributeOne = Attribute(name = "attributeType", value = Seq(MString("value")))
    val edgeOne = Edge.apply2("edge1Id", mReference, Seq(), Seq(), Seq(attributeOne))

    val attributeTwo = Attribute(name = "attributeType", value = Seq(MString("differentValue")))
    val edgeTwo = Edge.apply2("edge2Id", mReference, Seq(), Seq(), Seq(attributeTwo))

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be (2)
    results.forall(_.valid) should be (true)

    val attributeThree = Attribute(name = "attributeType", value = Seq(MString("anotherValue"), MString("yetAnotherValue")))
    val edgeThree = Edge.apply2("edge3Id", mReference, Seq(), Seq(), Seq(attributeThree))

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be (3)
    secondResults.forall(_.valid) should be (true)

  }

  it should "return failure validation results on invalid attributes" in {
    val attributeOne = Attribute(name = "attributeType", value = Seq(MString("duplicateValue")))
    val edgeOne = Edge.apply2("edge1Id", mReference, Seq(), Seq(), Seq(attributeOne))

    val attributeTwo = Attribute(name = "attributeType", value = Seq(MString("duplicateValue")))
    val edgeTwo = Edge.apply2("edge2Id", mReference, Seq(), Seq(), Seq(attributeTwo))

    val results = rule.check(Seq(edgeOne, edgeTwo))

    results.size should be (2)
    results.head.valid should be (false)
    results.last.valid should be (false)

    val attributeThree = Attribute(name = "attributeType", value = Seq(MString("duplicateValue"), MString("anotherValue")))
    val edgeThree = Edge.apply2("edge3Id", mReference, Seq(), Seq(), Seq(attributeThree))

    val secondResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree))

    secondResults.size should be (3)
    secondResults.forall(!_.valid) should be (true)

    val attributeFour = Attribute(name = "attributeType", value = Seq(MString("differentValue")))
    val edgeFour = Edge.apply2("edge4Id", mReference, Seq(), Seq(), Seq(attributeFour))

    val thirdResults = rule.check(Seq(edgeOne, edgeTwo, edgeThree, edgeFour))

    thirdResults.size should be (4)
    thirdResults.head.valid should be (false)
    thirdResults(1).valid should be (false)
    thirdResults(2).valid should be (false)
    thirdResults(3).valid should be (true)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inEdges "edgeType" areGlobalUnique ()""")
  }

}
