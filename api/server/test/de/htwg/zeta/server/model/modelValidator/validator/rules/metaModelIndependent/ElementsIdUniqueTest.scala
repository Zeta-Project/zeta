package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ElementsIdUniqueTest extends AnyFlatSpec with Matchers {

  val rule = new ElementsIdsUnique
  private val emptyString = ""

  val mReference = MReference(
    "edgeType",
    emptyString,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    emptyString,
    emptyString,
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    Seq.empty,
    Seq.empty
  )
  val mClass = MClass("nodeType", emptyString, abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "check" should "return true on elements with unique ids" in {
    val elements = GraphicalDslInstance.empty(emptyString, UUID.randomUUID())
      .copy(
        edges = Seq(
          EdgeInstance.empty("id1", mReference.name, emptyString, emptyString),
          EdgeInstance.empty("id3", mReference.name, emptyString, emptyString)
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
    val elements = GraphicalDslInstance.empty(emptyString, UUID.randomUUID())
      .copy(
        edges = Seq(
          EdgeInstance.empty(id1, mReference.name, emptyString, emptyString),
          EdgeInstance.empty(id1, mReference.name, emptyString, emptyString)
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
