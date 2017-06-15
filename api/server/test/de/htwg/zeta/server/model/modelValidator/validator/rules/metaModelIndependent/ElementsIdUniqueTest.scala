package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ElementsIdUniqueTest extends FlatSpec with Matchers {

  val rule = new ElementsIdsUnique
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set.empty)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "check" should "return true on elements with unique ids" in {
    val elements = Seq(
      Edge("id1", mReference, Set(), Set(), Map.empty),
      Node("id2", mClass, Set(), Set(), Map.empty),
      Edge("id3", mReference, Set(), Set(), Map.empty),
      Node("id4", mClass, Set(), Set(), Map.empty)
    )
    val results = rule.check(elements)
    results.forall(_.valid) should be (true)
  }

  it should "return false on elements with duplicate ids" in {
    val elements = Seq(
      Edge("id1", mReference, Set(), Set(), Map.empty),
      Node("id2", mClass, Set(), Set(), Map.empty),
      Edge("id2", mReference, Set(), Set(), Map.empty),
      Node("id1", mClass, Set(), Set(), Map.empty)
    )
    val results = rule.check(elements)
    results.forall(!_.valid) should be (true)
  }

}
