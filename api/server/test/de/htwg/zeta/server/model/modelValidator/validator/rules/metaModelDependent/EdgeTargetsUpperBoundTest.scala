package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetsUpperBoundTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgeTargetsUpperBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less target nodes of type targetType" in {
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


    val oneTargetNode = ToNodes(clazz = targetType, nodeNames = Seq("1"))

    val edgeOneTargetNode = Edge("", mReference, Seq(), Seq(oneTargetNode), Map.empty)

    rule.isValid(edgeOneTargetNode).get should be(true)


    val edgeNoTargetNodes = Edge("", mReference, Seq(), Seq(), Map.empty)

    rule.isValid(edgeNoTargetNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having more than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val threeTargetNodes = ToNodes(clazz = targetType, nodeNames = Seq("1", "2", "2"))

    val edgeThreeTargetNodes = Edge("", mReference, Seq(), Seq(threeTargetNodes), Map.empty)

    rule.isValid(edgeThreeTargetNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("", differentReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" toNodes "targetType" haveUpperBound 2""")
  }
}
