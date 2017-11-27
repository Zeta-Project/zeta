package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetsUpperBoundTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)
  val rule = new EdgeTargetsUpperBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val twoTargetNodes = NodeLink(className = targetType.name, nodeNames = Seq("", ""))

    val edgeTwoTargetNodes = emptyEdge.copy(targetNodeName = Seq(twoTargetNodes))

    rule.isValid(edgeTwoTargetNodes).get should be(true)


    val oneTargetNode = NodeLink(className = targetType.name, nodeNames = Seq(""))

    val edgeOneTargetNode = emptyEdge.copy(targetNodeName = Seq(oneTargetNode))

    rule.isValid(edgeOneTargetNode).get should be(true)


    val edgeNoTargetNodes = emptyEdge

    rule.isValid(edgeNoTargetNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having more than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val threeTargetNodes = NodeLink(className = targetType.name, nodeNames = Seq("", "", ""))

    val edgeThreeTargetNodes = emptyEdge.copy(targetNodeName = Seq(threeTargetNodes))

    rule.isValid(edgeThreeTargetNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentReference.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" toNodes "targetType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val targetLinkDef = MClassLinkDef(class1.name, 7, 0, deleteIfLower = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq(targetLinkDef),
      Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeTargetsUpperBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeTargetsUpperBound =>
        rule.edgeType should be("reference")
        rule.targetType should be("class")
        rule.upperBound should be(7)
      case _ => fail
    }
  }
}
