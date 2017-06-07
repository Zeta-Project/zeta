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

class EdgeTargetsUpperBoundTest extends FlatSpec with Matchers {
  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeTargetsUpperBound("edgeType", "targetType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less target nodes of type targetType" in {
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

    rule.isValid(edgeOneTargetNode).get should be (true)


    val edgeNoTargetNodes = Edge.apply2("", mReference, Seq(), Seq(), Seq())

    rule.isValid(edgeNoTargetNodes).get should be (true)
  }

  it should "return false on edges of type edgeType having more than 2 target nodes of type targetType" in {
    val targetType = MClass(
      name = "targetType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

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

    rule.isValid(edgeThreeTargetNodes).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Targets ofEdges "edgeType" toNodes "targetType" haveUpperBound 2""")
  }
}
