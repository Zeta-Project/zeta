package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeInputsUpperBoundTest extends FlatSpec with Matchers {

  val rule = new NodeInputsUpperBound("nodeType", "inputType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType having 2 or less input edges of type inputType" in {
    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val twoInputEdges = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeTwoInputEdge = Node.apply2("", mClass, Seq(), Seq(twoInputEdges), Seq())
    rule.isValid(nodeTwoInputEdge).get should be (true)

    val oneInputEdge = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeOneInputEdge = Node.apply2("", mClass, Seq(), Seq(oneInputEdge), Seq())
    rule.isValid(nodeOneInputEdge).get should be (true)

    val noInputEdges = ToEdges(inputType, Seq())
    val nodeNoInputEdges = Node.apply2("", mClass, Seq(), Seq(noInputEdges), Seq())
    rule.isValid(nodeNoInputEdges).get should be (true)
  }

  it should "return false on nodes of type nodeType having more than 2 input edges of type inputType" in {
    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val threeInputEdges = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeThreeInputEdges = Node.apply2("", mClass, Seq(), Seq(threeInputEdges), Seq())
    rule.isValid(nodeThreeInputEdges).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Inputs ofNodes "nodeType" toEdges "inputType" haveUpperBound 2""")
  }

}
