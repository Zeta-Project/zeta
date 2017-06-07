package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ElementsIdUniqueTest extends FlatSpec with Matchers {

  val rule = new ElementsIdsUnique
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "check" should "return true on elements with unique ids" in {
    val elements = Seq(
      Edge.apply2("id1", mReference, Seq(), Seq(), Seq()),
      Node.apply2("id2", mClass, Seq(), Seq(), Seq()),
      Edge.apply2("id3", mReference, Seq(), Seq(), Seq()),
      Node.apply2("id4", mClass, Seq(), Seq(), Seq())
    )
    val results = rule.check(elements)
    results.forall(_.valid) should be (true)
  }

  it should "return false on elements with duplicate ids" in {
    val elements = Seq(
      Edge.apply2("id1", mReference, Seq(), Seq(), Seq()),
      Node.apply2("id2", mClass, Seq(), Seq(), Seq()),
      Edge.apply2("id2", mReference, Seq(), Seq(), Seq()),
      Node.apply2("id1", mClass, Seq(), Seq(), Seq())
    )
    val results = rule.check(elements)
    results.forall(!_.valid) should be (true)
  }

}
