package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesTest extends FlatSpec with Matchers {

  val mReference1 = MReference(
    "edgeType1",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val mReference2 = MReference(
    "edgeType2",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val mReference3 = MReference(
    "edgeType3",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new Edges(Seq("edgeType1", "edgeType2"))

  "isValid" should "return true on valid edges" in {
    val edge1 = Edge.apply2("edgeId", mReference1, Seq(), Seq(), Seq())
    rule.isValid(edge1).get should be(true)

    val edge2 = Edge.apply2("edgeId", mReference2, Seq(), Seq(), Seq())
    rule.isValid(edge2).get should be(true)
  }

  it should "return false on invalid edges" in {
    val edge3 = Edge.apply2("edgeId", mReference3, Seq(), Seq(), Seq())
    rule.isValid(edge3).get should be(false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges areOfTypes Seq("edgeType1", "edgeType2")""")
  }

}
