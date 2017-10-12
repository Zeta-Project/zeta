package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

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
      Edge.empty("id1", mReference.name, Seq(), Seq()),
      Node.empty("id2", mClass.name, Seq(), Seq()),
      Edge.empty("id3", mReference.name, Seq(), Seq()),
      Node.empty("id4", mClass.name, Seq(), Seq())
    )
    val results = rule.check(elements)
    results.forall(_.valid) should be (true)
  }

  it should "return false on elements with duplicate ids" in {
    val id1 = "name1"
    val id2 = "name2"
    val elements = Seq(
      Edge.empty(id1, mReference.name, Seq(), Seq()),
      Node.empty(id2, mClass.name, Seq(), Seq()),
      Edge.empty(id2, mReference.name, Seq(), Seq()),
      Node.empty(id1, mClass.name, Seq(), Seq())
    )
    val results = rule.check(elements)
    results.forall(!_.valid) should be (true)
  }

}
