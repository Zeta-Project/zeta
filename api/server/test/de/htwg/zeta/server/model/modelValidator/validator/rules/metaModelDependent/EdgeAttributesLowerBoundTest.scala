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

class EdgeAttributesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributesLowerBound("edgeType", "attributeType", 2)

  "isValid" should "return true on edges with 2 or more attributes of type attributeType" in {
    val twoAttributes = Attribute(name = "attributeType", value = Seq(MString("att1"), MString("att2")))
    val twoAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(twoAttributes))

    rule.isValid(twoAttributesEdge).get should be (true)

    val threeAttributes = Attribute(name = "attributeType", value = Seq(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(threeAttributes))

    rule.isValid(threeAttributesEdge).get should be (true)
  }

  it should "return false on edges with less than 2 attributes of type attributeType" in {
    val noAttributes = Attribute(name = "attributeType", value = Seq())
    val noAttributesEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(noAttributes))

    rule.isValid(noAttributesEdge).get should be (false)

    val oneAttribute = Attribute(name = "attributeType", value = Seq(MString("att")))
    val oneAttributeEdge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(oneAttribute))

    rule.isValid(oneAttributeEdge).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("edgeId", differentReference, Seq(), Seq(), Seq())

    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be("""Attributes ofType "attributeType" inEdges "edgeType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 5)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](attribute))
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgeAttributesLowerBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeAttributesLowerBound =>
        rule.edgeType should be ("reference")
        rule.attributeType should be ("attributeName")
        rule.lowerBound should be (5)
      case _ => fail
    }
  }

}
