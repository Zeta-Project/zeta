package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeOutputsUpperBoundTest extends FlatSpec with Matchers {
  val rule = new NodeOutputsUpperBound("nodeType", "outputType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType having 2 or less output edges of type outputType" in {
    val outputType = MReference("outputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val twoOutputEdges = ToEdges(outputType, Seq(outputType.name, outputType.name))
    val nodeTwoOutputEdges = Node("", mClass, Seq(twoOutputEdges), Seq(), Map.empty)
    rule.isValid(nodeTwoOutputEdges).get should be(true)

    val oneOutputEdge = ToEdges(outputType, Seq(outputType.name))
    val nodeOneOutputEdge = Node("", mClass, Seq(oneOutputEdge), Seq(), Map.empty)
    rule.isValid(nodeOneOutputEdge).get should be(true)

    val noOutputEdges = ToEdges(outputType, Seq())
    val nodeNoOutputEdges = Node("", mClass, Seq(noOutputEdges), Seq(), Map.empty)
    rule.isValid(nodeNoOutputEdges).get should be(true)
  }

  it should "return false on nodes of type nodeType having more than 2 output edges of type outputType" in {
    val outputType = MReference("outputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val threeOutputEdges = ToEdges(outputType, Seq(outputType.name, outputType.name, outputType.name))
    val nodeThreeOutputEdges = Node("", mClass, Seq(threeOutputEdges), Seq(), Map.empty)
    rule.isValid(nodeThreeOutputEdges).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" toEdges "outputType" haveUpperBound 2""")
  }
}
