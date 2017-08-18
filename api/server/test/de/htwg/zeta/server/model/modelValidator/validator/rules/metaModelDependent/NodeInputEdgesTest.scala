package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

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

  "isValid" should "return true on nodes of type nodeType with valid input edges" in {

    val input1 = MReference("input1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges1 = EdgeLink(referenceName = input1.name, edgeIds = Seq(UUID.randomUUID()))
    val node1 = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(toEdges1), Map.empty)
    rule.isValid(node1).get should be (true)

    val input2 = MReference("input2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges2 = EdgeLink(referenceName = input1.name, edgeIds = Seq(UUID.randomUUID(), UUID.randomUUID()))
    val node2 = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(toEdges2), Map.empty)
    rule.isValid(node2).get should be (true)
  }

  it should "return false on nodes of type nodeType with invalid input edges" in {

    val input = MReference("invalid", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges = EdgeLink(referenceName = input.name, edgeIds = Seq(UUID.randomUUID()))
    val node = Node(UUID.randomUUID(), mClass.name, Seq(), Seq(toEdges), Map.empty)
    rule.isValid(node).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentClass = MClass("differentNodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = Node(UUID.randomUUID(), differentClass.name, Seq(), Seq(), Map.empty)
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
