package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourcesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeSourcesLowerBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val twoSourceNodes = ToNodes(clazz = sourceType, nodeNames = Seq("1", "2"))

    val edgeTwoSourceNodes = Edge("", mReference, Seq(twoSourceNodes), Seq(), Map.empty)

    rule.isValid(edgeTwoSourceNodes).get should be(true)

    val threeSourceNodes = ToNodes(clazz = sourceType, nodeNames = Seq("1", "2", "2"))

    val edgeThreeSourceNodes = Edge("", mReference, Seq(threeSourceNodes), Seq(), Map.empty)

    rule.isValid(edgeThreeSourceNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having less than 2 source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val oneSourceNode = ToNodes(clazz = sourceType, nodeNames = Seq("1"))

    val edgeOneSourceNode = Edge("", mReference, Seq(oneSourceNode), Seq(), Map.empty)

    rule.isValid(edgeOneSourceNode).get should be(false)

    val edgeNoSourceNodes = Edge("", mReference, Seq(), Seq(), Map.empty)

    rule.isValid(edgeNoSourceNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMRef = MReference(
      "invalidReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("", differentMRef, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" toNodes "sourceType" haveLowerBound 2""")
  }

}