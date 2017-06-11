package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourceNodesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeSourceNodes("edgeType", Seq("source1", "source2"))

  "isValid" should "return true on edges of type edgeType with valid source nodes" in {

    val source1 = MClass(
      name = "source1",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val toNodes1 = ToNodes(clazz = source1, nodeNames = Seq(""))

    val edge1 = Edge("", mReference, Seq(toNodes1), Seq(), Map.empty)

    rule.isValid(edge1).get should be(true)

    val source2 = MClass(
      name = "source2",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val toNodes2 = ToNodes(clazz = source1, nodeNames = Seq("", ""))

    val edge2 = Edge("", mReference, Seq(toNodes2), Seq(), Map.empty)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid source nodes" in {
    val invalidSource = MClass(
      name = "invalidSource",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val invalidToNodes = ToNodes(clazz = invalidSource, nodeNames = Seq(""))

    val edge1 = Edge("", mReference, Seq(invalidToNodes), Seq(), Map.empty)

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("", differentMReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" areOfTypes Seq("source1", "source2")""")
  }

}
