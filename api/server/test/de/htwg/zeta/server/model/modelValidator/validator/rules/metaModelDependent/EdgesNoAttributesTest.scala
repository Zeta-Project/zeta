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

class EdgesNoAttributesTest extends FlatSpec with Matchers {
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgesNoAttributes("edgeType")

  "isValid" should "return true on edges of type edgeType with no attributes" in {
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq())
    rule.isValid(edge).get should be (true)
  }

  it should "return false on edges of type edgeType with attributes" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MString("att")))
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq(attribute))
    rule.isValid(edge).get should be (false)
  }

  it should "return true on edges of type edgeType with empty attribute values" in {
    val attribute = Attribute(name = "attributeType", value = Seq())
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq(attribute))
    rule.isValid(edge).get should be (true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Edges ofType "edgeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgesNoAttributes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgesNoAttributes =>
        rule.edgeType should be ("reference")
      case _ => fail
    }
  }
}
