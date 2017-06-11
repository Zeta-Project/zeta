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

class NodeInputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeInputEdges("nodeType", Seq("input1", "input2"))
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with valid input edges" in {

    val input1 = MReference("input1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges1 = ToEdges(reference = input1, edgeNames = Seq(input1.name))
    val node1 = Node("", mClass, Seq(), Seq(toEdges1), Map.empty)
    rule.isValid(node1).get should be (true)

    val input2 = MReference("input2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges2 = ToEdges(reference = input1, edgeNames = Seq(input1.name, input2.name))
    val node2 = Node("", mClass, Seq(), Seq(toEdges2), Map.empty)
    rule.isValid(node2).get should be (true)
  }

  it should "return false on nodes of type nodeType with invalid input edges" in {

    val input = MReference("invalid", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges = ToEdges(reference = input, edgeNames = Seq(input.name))
    val node = Node("", mClass, Seq(), Seq(toEdges), Map.empty)
    rule.isValid(node).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentClass, Seq(), Seq(), Map.empty)
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Inputs ofNodes "nodeType" areOfTypes Seq("input1", "input2")""")
  }


}
