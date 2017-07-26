package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ElementsIdUniqueTest extends FlatSpec with Matchers {

  val rule = new ElementsIdsUnique
  val mReference = MReference("edgeType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq.empty,
    Seq.empty)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "check" should "return true on elements with unique ids" in {
    val elements = Seq(
      Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty),
      Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), Map.empty),
      Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty),
      Node(UUID.randomUUID(), mClass.name, Seq(), Seq(), Map.empty)
    )
    val results = rule.check(elements)
    results.forall(_.valid) should be (true)
  }

  it should "return false on elements with duplicate ids" in {
    val id1 = UUID.randomUUID()
    val id2 = UUID.randomUUID()
    val elements = Seq(
      Edge(id1, mReference.name, Seq(), Seq(), Map.empty),
      Node(id2, mClass.name, Seq(), Seq(), Map.empty),
      Edge(id2, mReference.name, Seq(), Seq(), Map.empty),
      Node(id1, mClass.name, Seq(), Seq(), Map.empty)
    )
    val results = rule.check(elements)
    results.forall(!_.valid) should be (true)
  }

}
