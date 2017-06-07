package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "isValid" should "return true on non-empty attribute names" in {
    val attribute = Attribute("attributeName1", Seq())
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq(attribute))
    rule.isValid(edge).get should be (true)
  }

  it should "return false on empty attribute names" in {
    val attribute = Attribute("", Seq())
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq(attribute))
    rule.isValid(edge).get should be (false)
  }

}
