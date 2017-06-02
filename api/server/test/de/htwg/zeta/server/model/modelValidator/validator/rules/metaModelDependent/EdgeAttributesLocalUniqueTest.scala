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

class EdgeAttributesLocalUniqueTest extends FlatSpec with Matchers {

  val rule = new EdgeAttributesLocalUnique("edgeType", "attributeType")
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "isValid" should "return true on valid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MString("valueOne"), MString("valueTwo"), MString("valueThree")))
    val edge = Edge.apply2("edgeOneId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(true)
  }

  it should "return false on invalid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MString("dupValue"), MString("dupValue"), MString("valueThree")))
    val edge = Edge.apply2("edgeOneId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val mReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("edgeOneId", mReference, Seq(), Seq(), Seq())

    rule.isValid(edge) should be(None)
  }

}
