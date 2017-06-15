package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ElementsIdsNotEmptyTest extends FlatSpec with Matchers {
  val rule = new ElementsIdsNotEmpty
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set.empty)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "check" should "return true on elements with non-empty ids" in {
    val elements = Seq(
      Node("nodeId", mClass, Set(), Set(), Map.empty),
      Edge("edgeId", mReference, Set(), Set(), Map.empty)
    )
    val results = rule.check(elements)
    results.forall(_.valid) should be (true)
  }

  it should "return false on elements with empty ids" in {
    val elements = Seq(
      Node("", mClass, Set(), Set(), Map.empty),
      Edge("", mReference, Set(), Set(), Map.empty)
    )
    val results = rule.check(elements)
    results.head.valid should be (false)
    results.last.valid should be (false)
  }
}
