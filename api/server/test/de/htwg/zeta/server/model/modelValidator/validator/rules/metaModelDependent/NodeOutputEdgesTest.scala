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

class NodeOutputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeOutputEdges("nodeType", Seq("output1", "output2"))
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with valid output edges" in {

    val output1 = MReference("output1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges1 = ToEdges(output1, Seq(
      Edge.apply2("", output1, Seq(), Seq(), Seq())
    ))
    val node1 = Node.apply2("", mClass, Seq(toEdges1), Seq(), Seq())
    rule.isValid(node1).get should be(true)

    val output2 = MReference("output2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges2 = ToEdges(output2, Seq(
      Edge.apply2("", output1, Seq(), Seq(), Seq()),
      Edge.apply2("", output2, Seq(), Seq(), Seq())
    ))
    val node2 = Node.apply2("", mClass, Seq(toEdges2), Seq(), Seq())
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid output edges" in {
    val output = MReference("invalid", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges = ToEdges(output, Seq(
      Edge.apply2("", output, Seq(), Seq(), Seq())
    ))
    val node = Node.apply2("", mClass, Seq(toEdges), Seq(), Seq())
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" areOfTypes Seq("output1", "output2")""")
  }

}
