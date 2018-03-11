package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeInputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeInputEdges("nodeType", Seq("input1", "input2"))
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with valid input edges" in {

    val input1 = MReference("input1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node1 = emptyNode.copy(inputEdgeNames = Seq(input1.name))
    rule.isValid(node1).get should be(true)

    val input2 = MReference("input2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node2 = emptyNode.copy(inputEdgeNames = Seq(input1.name))
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid input edges" in {

    val input = MReference("invalid", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node = emptyNode.copy(inputEdgeNames = Seq(input.name))
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Inputs ofNodes "nodeType" areOfTypes Seq("input1", "input2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val mReference2 = MReference("reference2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq(mReference1.name, mReference2.name), Seq.empty, Seq.empty, Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodeInputEdges.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeInputEdges =>
        rule.nodeType should be("class")
        rule.inputTypes should be(Seq("reference1", "reference2"))
      case _ => fail
    }

  }


}
