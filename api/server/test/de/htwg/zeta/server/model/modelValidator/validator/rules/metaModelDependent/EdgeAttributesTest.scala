package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeAttributes("reference", Seq("stringAttribute", "boolAttribute"))

  "the rule" should "be true for valid edge" in {
    val attributes = Map(
      "stringAttribute" -> Seq(MString("test")),
      "boolAttribute" -> Seq(MBool(true))
    )
    val edge = Edge("edgeId", mReference, Seq(), Seq(), attributes)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attributes = Map(
      "stringAttribute" -> Seq(MString("test")),
      "boolAttribute" -> Seq(MBool(true)),
      "invalidAttribute" -> Seq(MDouble(1.0))
    )

    val edge = Edge("edgeId", mReference, Seq(), Seq(), attributes)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {

    val nonMatchingReference = MReference(
      "nonMatchingReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )

    val attributes = Map(
      "stringAttribute" -> Seq(MString("test")),
      "boolAttribute" -> Seq(MBool(true))
    )

    val edge = Edge("edgeId", nonMatchingReference, Seq(), Seq(), attributes)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inEdges "reference" areOfTypes Seq("stringAttribute", "boolAttribute")""")
  }

}