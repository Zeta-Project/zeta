package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeOutputEdgesTest extends FlatSpec with Matchers {

  val rule = new NodeOutputEdges("nodeType", Seq("output1", "output2"))
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with valid output edges" in {

    val output1 = MReference("output1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val toEdges1 = ToEdges(output1, Set(output1.name))
    val node1 = Node("", mClass, Set(toEdges1), Set(), Map.empty)
    rule.isValid(node1).get should be(true)

    val output2 = MReference("output2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val toEdges2 = ToEdges(output2, Set(output1.name, output2.name))
    val node2 = Node("", mClass, Set(toEdges2), Set(), Map.empty)
    rule.isValid(node2).get should be(true)
  }

  it should "return false on nodes of type nodeType with invalid output edges" in {
    val output = MReference("invalid", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val toEdges = ToEdges(output, Set(output.name))
    val node = Node("", mClass, Set(toEdges), Set(), Map.empty)
    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentClass, Set(), Set(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" areOfTypes Set("output1", "output2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference1 = MReference("reference1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val mReference2 = MReference("reference2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val outputMLinkDef1 = MReferenceLinkDef(mReference1.name, -1, 0, deleteIfLower = false)
    val outputMLinkDef2 = MReferenceLinkDef(mReference2.name, -1, 0, deleteIfLower = false)

    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set(outputMLinkDef1, outputMLinkDef2), Set[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeOutputEdges.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeOutputEdges =>
        rule.nodeType should be("class")
        rule.outputTypes should be(Set("reference1", "reference2"))
      case _ => fail
    }

  }

}
