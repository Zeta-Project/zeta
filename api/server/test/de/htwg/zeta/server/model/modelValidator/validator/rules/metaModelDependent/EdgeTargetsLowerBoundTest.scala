package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
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

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val targetLinkDef = MClassLinkDef(class1.name, -1, 5, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq(targetLinkDef),
      Seq[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeTargetsLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeTargetsLowerBound =>
        rule.edgeType should be("reference")
        rule.targetType should be("class")
        rule.lowerBound should be(5)
      case _ => fail
    }
  }
}
