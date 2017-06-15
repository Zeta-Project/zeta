package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeOutputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeOutputEdges("nodeType", Seq("output1", "output2"))
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with valid output edges" in {

    val output1 = MReference("output1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges1 = ToEdges(output1, Seq(output1.name))
    val node1 = Node("", mClass, Seq(toEdges1), Seq(), Map.empty)
    rule.isValid(node1).get should be(true)

    val output2 = MReference("output2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges2 = ToEdges(output2, Seq(output1.name, output2.name))
    val node2 = Node("", mClass, Seq(toEdges2), Seq(), Map.empty)
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid output edges" in {
    val output = MReference("invalid", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdges = ToEdges(output, Seq(output.name))
    val node = Node("", mClass, Seq(toEdges), Seq(), Map.empty)
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentClass, Seq(), Seq(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" areOfTypes Seq("output1", "output2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val mReference2 = MReference("reference2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val outputMLinkDef1 = MLinkDef(mReference1, -1, 0, deleteIfLower = false)
    val outputMLinkDef2 = MLinkDef(mReference2, -1, 0, deleteIfLower = false)

    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](outputMLinkDef1, outputMLinkDef2), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeOutputEdges.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeOutputEdges =>
        rule.nodeType should be ("class")
        rule.outputTypes should be (Seq("reference1", "reference2"))
      case _ => fail
    }

  }

}
