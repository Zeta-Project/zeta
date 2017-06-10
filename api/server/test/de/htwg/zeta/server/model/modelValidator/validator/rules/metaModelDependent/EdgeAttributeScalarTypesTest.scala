package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeScalarTypesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeAttributeScalarTypes("reference", "attributeType", ScalarType.String)

  "the rule" should "be true for valid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MString("value")))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MInt(42)))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(false)
  }

  it should "return None for non-matching edges" in {
    val differentMReference = MReference(
      "differentMReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val attribute = Attribute(name = "attributeType", value = Seq(MString("value")))
    val edge = Edge.apply2("edgeId", differentMReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfScalarType "String"""")
  }

}
