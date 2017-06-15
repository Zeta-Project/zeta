package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeName1" -> Set())
    val edge = Edge("", mReference, Set(), Set(), attribute)
    rule.isValid(edge).get should be (true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("" -> Set())
    val edge = Edge("", mReference, Set(), Set(), attribute)
    rule.isValid(edge).get should be (false)
  }

}
