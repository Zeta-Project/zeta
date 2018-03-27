package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
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
          EdgeInstance.empty("id1", mReference.name, "", ""),
          EdgeInstance.empty("id3", mReference.name, "", "")
        ),
        nodes = Seq(
          NodeInstance.empty("id2", mClass.name, Seq(), Seq()),

          NodeInstance.empty("id4", mClass.name, Seq(), Seq())
        )
      )

    val results = rule.check(elements)
    results should be(true)
  }

  it should "return false on elements with duplicate ids" ignore {
    val id1 = "name1"
    val id2 = "name2"
    val elements = GraphicalDslInstance.empty("", UUID.randomUUID())
      .copy(
        edges = Seq(
          EdgeInstance.empty(id1, mReference.name, "", ""),
          EdgeInstance.empty(id1, mReference.name, "", "")
        ),
        nodes = Seq(
          NodeInstance.empty(id2, mClass.name, Seq(), Seq()),
          NodeInstance.empty(id2, mClass.name, Seq(), Seq())
        )
      )
    val results = rule.check(elements)
    results should be(false)
  }

}
