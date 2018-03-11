package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ElementsIdUniqueTest extends FlatSpec with Matchers {

  val rule = new ElementsIdsUnique
  val mReference = MReference("edgeType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq.empty,
    Seq.empty)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "check" should "return true on elements with unique ids" in {
    val elements = GraphicalDslInstance.empty("", UUID.randomUUID())
      .copy(
        edges = Seq(
          Edge.empty("id1", mReference.name, "", ""),
          Edge.empty("id3", mReference.name, "", "")
        ),
        nodes = Seq(
          Node.empty("id2", mClass.name, Seq(), Seq()),

          Node.empty("id4", mClass.name, Seq(), Seq())
        )
      )

    val results = rule.check(elements)
    results should be(true)
  }

  it should "return false on elements with duplicate ids" in {
    val id1 = "name1"
    val id2 = "name2"
    val elements = GraphicalDslInstance.empty("", UUID.randomUUID())
      .copy(
        edges = Seq(
          Edge.empty(id1, mReference.name, "", ""),
          Edge.empty(id1, mReference.name, "", "")
        ),
        nodes = Seq(
          Node.empty(id2, mClass.name, Seq(), Seq()),
          Node.empty(id2, mClass.name, Seq(), Seq())
        )
      )
    val results = rule.check(elements)
    results should be(false)
  }

}
