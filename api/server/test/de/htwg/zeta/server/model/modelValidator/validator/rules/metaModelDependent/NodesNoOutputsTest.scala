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

class NodesNoOutputsTest extends FlatSpec with Matchers {
  val rule = new NodesNoOutputs("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no outputs" in {
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq())
    rule.isValid(node).get should be (true)
  }

  it should "return false on nodes of type nodeType with outputs" in {
    val output = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdge = ToEdges(output, Seq(
      Edge.apply2("", output, Seq(), Seq(), Seq())
    ))
    val node = Node.apply2("", mClass, Seq(toEdge), Seq(), Seq())
    rule.isValid(node).get should be (false)
  }

  it should "return true on nodes of type nodeType with empty output list" in {
    val output = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdge = ToEdges(output, Seq())
    val node = Node.apply2("", mClass, Seq(toEdge), Seq(), Seq())
    rule.isValid(node).get should be (true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Nodes ofType "nodeType" haveNoOutputs ()""")
  }
}
