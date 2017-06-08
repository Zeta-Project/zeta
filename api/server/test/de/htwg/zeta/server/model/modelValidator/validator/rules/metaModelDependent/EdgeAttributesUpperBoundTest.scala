package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesUpperBoundTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributesUpperBound("edgeType", "attributeType", 2)

  "check" should "return true on edges with 2 or less attributes of type attributeType" in {

    val noAttributes = Attribute(name = "attributeType", value = Seq())
    val noAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(noAttributes))

    rule.isValid(noAttributesEdge).get should be (true)

    val oneAttribute = Attribute(name = "attributeType", value = Seq(MString("att")))
    val oneAttributeEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(oneAttribute))

    rule.isValid(oneAttributeEdge).get should be (true)

    val twoAttributes = Attribute(name = "attributeType", value = Seq(MString("att1"), MString("att2")))
    val twoAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(twoAttributes))

    rule.isValid(twoAttributesEdge).get should be (true)

  }

  it should "return false on edges with more than 2 attributes of type attributeType" in {
    val threeAttributes = Attribute(name = "attributeType", value = Seq(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(threeAttributes))

    rule.isValid(threeAttributesEdge).get should be (false)

    val fourAttributes = Attribute(name = "attributeType", value = Seq(MString("att1"), MString("att2"), MString("att3"), MString("att4")))
    val fourAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(fourAttributes))

    rule.isValid(fourAttributesEdge).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("edgeId", differentReference, Seq(), Seq(), Seq())

    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be("""Attributes ofType "attributeType" inEdges "edgeType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, 7, 0)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](attribute))
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgeAttributesUpperBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeAttributesUpperBound =>
        rule.edgeType should be ("reference")
        rule.attributeType should be ("attributeName")
        rule.upperBound should be (7)
      case _ => fail
    }
  }

}
