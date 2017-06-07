package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesTest extends FlatSpec with Matchers {

  val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributes("reference", Seq("stringAttribute", "boolAttribute"))

  "the rule" should "be true for valid edge" in {
    val attributes = Seq(
      Attribute(name = "stringAttribute", value = Seq(MString("test"))),
      Attribute(name = "boolAttribute", value = Seq(MBool(true)))
    )
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), attributes)

    rule.isValid(edge).get should be (true)
  }

  it should "be false for invalid edges" in {
    val attributes = Seq(
      Attribute(name = "stringAttribute", value = Seq(MString("test"))),
      Attribute(name = "boolAttribute", value = Seq(MBool(true))),
      Attribute(name = "invalidAttribute", value = Seq(MDouble(1.0)))

    )

    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), attributes)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {

    val nonMatchingReference = MReference("nonMatchingReference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

    val attributes = Seq(
      Attribute(name = "stringAttribute", value = Seq(MString("test"))),
      Attribute(name = "boolAttribute", value = Seq(MBool(true)))
    )

    val edge = Edge.apply2("edgeId", nonMatchingReference, Seq(), Seq(), attributes)

    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes inEdges "reference" areOfTypes Seq("stringAttribute", "boolAttribute")""")
  }

}
