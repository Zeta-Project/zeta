package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributeSetTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributeSet
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())


  "isValid" should "return true on valid edges attribute sets" in {

    val attributes = Seq(
      Attribute("attributeName1", Seq()),
      Attribute("attributeName2", Seq())
    )
    val edge = Edge.apply2("", mReference, Seq(), Seq(), attributes)
    rule.isValid(edge).get should be(true)
  }


  it should "return false on invalid edges attribute sets" in {
    val attributes = Seq(
      Attribute("duplicateAttributeName", Seq()),
      Attribute("duplicateAttributeName", Seq())
    )
    val edge = Edge.apply2("", mReference, Seq(), Seq(), attributes)
    rule.isValid(edge).get should be(false)
  }

}
