package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeInputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeInputEdges("nodeType", Seq("input1", "input2"))
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with valid input edges" in {

    val input1 = MReference("input1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges1 = EdgeLink(referenceName = input1.name, edgeNames = Seq(""))
    val node1 = emptyNode.copy(inputs = Seq(toEdges1))
    rule.isValid(node1).get should be (true)

    val input2 = MReference("input2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges2 = EdgeLink(referenceName = input1.name, edgeNames = Seq("", ""))
    val node2 = emptyNode.copy(inputs = Seq(toEdges2))
    rule.isValid(node2).get should be (true)
  }

  it should "return false on nodes of type nodeType with invalid input edges" in {

    val input = MReference("invalid", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges = EdgeLink(referenceName = input.name, edgeNames = Seq(""))
    val node = emptyNode.copy(inputs = Seq(toEdges))
    rule.isValid(node).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Inputs ofNodes "nodeType" areOfTypes Seq("input1", "input2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val mReference2 = MReference("reference2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val inputMLinkDef1 = MReferenceLinkDef(mReference1.name, -1, 0, deleteIfLower = false)
    val inputMLinkDef2 = MReferenceLinkDef(mReference2.name, -1, 0, deleteIfLower = false)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq(inputMLinkDef1, inputMLinkDef2),Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeInputEdges.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeInputEdges =>
        rule.nodeType should be ("class")
        rule.inputTypes should be (Seq("reference1", "reference2"))
      case _ => fail
    }

  }


}
