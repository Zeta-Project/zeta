package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributeSetTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributeSet
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set.empty)


  "isValid" should "return true on valid edges attribute sets" in {

    val attributes: Map[String, Set[AttributeValue]] = Map(
      "attributeName1" -> Set(),
      "attributeName2" -> Set()
    )
    val edge = Edge("", mReference, Set(), Set(), attributes)
    rule.isValid(edge).get should be(true)
  }


  it should "return false on invalid edges attribute sets" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "duplicateAttributeName" -> Set(),
      "duplicateAttributeName" -> Set()
    )
    val edge = Edge("", mReference, Set(), Set(), attributes)
    rule.isValid(edge).get should be(false)
  }

}
