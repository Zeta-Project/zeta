package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  val mReference = MReference("edgeType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty,
    Seq.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeName1" -> Seq())
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be (true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("" -> Seq())
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), attribute)
    rule.isValid(edge).get should be (false)
  }

}
