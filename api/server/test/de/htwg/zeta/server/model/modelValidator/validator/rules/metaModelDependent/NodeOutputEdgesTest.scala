package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.project.concept.Concept
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeOutputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeOutputEdges("nodeType", Seq("output1", "output2"))
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with valid output edges" in {

    val output1 = MReference("output1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node1 = emptyNode.copy(outputEdgeNames = Seq(output1.name))
    rule.isValid(node1).get should be(true)

    val output2 = MReference("output2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node2 = emptyNode.copy(outputEdgeNames = Seq(output2.name))
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid output edges" in {
    val output = MReference("invalid", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node = emptyNode.copy(outputEdgeNames = Seq(output.name))
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" areOfTypes Seq("output1", "output2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val mReference2 = MReference("reference2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq(mReference1.name, mReference1.name), Seq[MAttribute](),
      Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodeOutputEdges.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeOutputEdges =>
        rule.nodeType should be("class")
        rule.outputTypes should be(Seq("reference1", "reference2"))
      case _ => fail
    }

  }

}
