package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesTest extends FlatSpec with Matchers {

  val mReference1 = MReference(
    "edgeType1",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )
  val mReference2 = MReference(
    "edgeType2",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )
  val mReference3 = MReference(
    "edgeType3",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new Edges(Seq("edgeType1", "edgeType2"))

  "isValid" should "return true on valid edges" in {
    val edge1 = Edge.empty("", mReference1.name, "", "")
    rule.isValid(edge1).get should be(true)

    val edge2 = Edge.empty("", mReference2.name, "", "")
    rule.isValid(edge2).get should be(true)
  }

  it should "return false on invalid edges" in {
    val edge3 = Edge.empty("", mReference3.name, "", "")
    rule.isValid(edge3).get should be(false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges areOfTypes Seq("edgeType1", "edgeType2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference1 = MReference("reference1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "",
      Seq[MAttribute](), Seq.empty)
    val reference2 = MReference("reference2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "",
      Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(references = Seq(reference1, reference2))
    val result = Edges.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: Edges =>
        rule.edgeTypes should be (Seq("reference1", "reference2"))
      case _ => fail
    }
  }

}
