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

class NodeOutputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeOutputEdges("nodeType", Seq("output1", "output2"))
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "isValid" should "return true on nodes of type nodeType with valid output edges" in {

    val output1 = MReference("output1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges1 = EdgeLink(output1.name, Seq(UUID.randomUUID()))
    val node1 = Node(UUID.randomUUID(), mClass.name, Seq(toEdges1), Seq(), Map.empty)
    rule.isValid(node1).get should be(true)

    val output2 = MReference("output2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges2 = EdgeLink(output2.name, Seq(UUID.randomUUID(), UUID.randomUUID()))
    val node2 = Node(UUID.randomUUID(), mClass.name, Seq(toEdges2), Seq(), Map.empty)
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid output edges" in {
    val output = MReference("invalid", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val toEdges = EdgeLink(output.name, Seq(UUID.randomUUID()))
    val node = Node(UUID.randomUUID(), mClass.name, Seq(toEdges), Seq(), Map.empty)
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentClass = MClass("differentNodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val node = Node(UUID.randomUUID(), differentClass.name, Seq(), Seq(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" areOfTypes Seq("output1", "output2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val mReference2 = MReference("reference2", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val outputMLinkDef1 = MReferenceLinkDef(mReference1.name, -1, 0, deleteIfLower = false)
    val outputMLinkDef2 = MReferenceLinkDef(mReference2.name, -1, 0, deleteIfLower = false)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq(outputMLinkDef1, outputMLinkDef2), Seq[MAttribute](),
      Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
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
