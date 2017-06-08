package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetsLowerBoundTest extends FlatSpec with Matchers {
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeTargetsLowerBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val twoTargetNodes = ToNodes(`type` = targetType, nodes = Seq(
      Node(
        id = "1",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeTwoTargetNodes = Edge.apply2("", mReference, Seq(), Seq(twoTargetNodes), Seq())

    rule.isValid(edgeTwoTargetNodes).get should be (true)

    val threeTargetNodes = ToNodes(`type` = targetType, nodes = Seq(
      Node(
        id = "1",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeThreeTargetNodes = Edge.apply2("", mReference, Seq(), Seq(threeTargetNodes), Seq())

    rule.isValid(edgeThreeTargetNodes).get should be (true)
  }

  it should "return false on edges of type edgeType having less than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val oneTargetNode = ToNodes(`type` = targetType, nodes = Seq(
      Node(
        id = "1",
        `type` = targetType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeOneTargetNode = Edge.apply2("", mReference, Seq(), Seq(oneTargetNode), Seq())

    rule.isValid(edgeOneTargetNode).get should be (false)

    val edgeNoTargetNodes = Edge.apply2("", mReference, Seq(), Seq(), Seq())

    rule.isValid(edgeNoTargetNodes).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentMRef = MReference("invalidReference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentMRef, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Targets ofEdges "edgeType" toNodes "targetType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val targetLinkDef = MLinkDef(class1, -1, 5, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](targetLinkDef), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgeTargetsLowerBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeTargetsLowerBound =>
        rule.edgeType should be ("reference")
        rule.targetType should be ("class")
        rule.lowerBound should be (5)
      case _ => fail
    }
  }
}
