package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoAttributesTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgesNoAttributes("edgeType")

  "isValid" should "return true on edges of type edgeType with no attributes" in {
    val edge = Edge("", mReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with attributes" in {
    val attribute = Map("attributeType" -> Seq(MString("att")))
    val edge = Edge("", mReference, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty attribute values" in {
    val attribute = Map("attributeType" -> Seq())
    val edge = Edge("", mReference, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
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
      """Edges ofType "edgeType" haveNoAttributes ()""")
  }
}