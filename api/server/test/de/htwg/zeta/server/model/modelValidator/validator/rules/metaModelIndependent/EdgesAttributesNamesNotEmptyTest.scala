package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute = Map("attributeName1" -> Seq())
    val edge = Edge("", mReference, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be (true)
  }

  it should "return false on empty attribute names" in {
    val attribute = Map("" -> Seq())
    val edge = Edge("", mReference, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be (false)
  }

}
