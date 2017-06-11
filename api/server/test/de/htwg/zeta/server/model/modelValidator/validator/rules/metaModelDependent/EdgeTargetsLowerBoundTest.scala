package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetsLowerBoundTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeTargetsLowerBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val twoTargetNodes = ToNodes(clazz = targetType, nodeNames = Seq("1", "2"))

    val edgeTwoTargetNodes = Edge("", mReference, Seq(), Seq(twoTargetNodes), Map.empty)

    rule.isValid(edgeTwoTargetNodes).get should be(true)

    val threeTargetNodes = ToNodes(clazz = targetType, nodeNames = Seq("1", "2", "2"))

    val edgeThreeTargetNodes = Edge("", mReference, Seq(), Seq(threeTargetNodes), Map.empty)

    rule.isValid(edgeThreeTargetNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having less than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val oneTargetNode = ToNodes(clazz = targetType, nodeNames = Seq("1"))

    val edgeOneTargetNode = Edge("", mReference, Seq(), Seq(oneTargetNode), Map.empty)

    rule.isValid(edgeOneTargetNode).get should be(false)

    val edgeNoTargetNodes = Edge("", mReference, Seq(), Seq(), Map.empty)

    rule.isValid(edgeNoTargetNodes).get should be(false)
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
      """Targets ofEdges "edgeType" toNodes "targetType" haveLowerBound 2""")
  }
}
