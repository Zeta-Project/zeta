package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeOutputsUpperBoundTest extends FlatSpec with Matchers {
  val rule = new NodeOutputsUpperBound("nodeType", "outputType", 2)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes of type nodeType having 2 or less output edges of type outputType" in {
    val outputType = MReference("outputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val twoOutputEdges = ToEdges(outputType, Set(outputType.name, outputType.name))
    val nodeTwoOutputEdges = Node("", mClass, Set(twoOutputEdges), Set(), Map.empty)
    rule.isValid(nodeTwoOutputEdges).get should be(true)

    val oneOutputEdge = ToEdges(outputType, Set(outputType.name))
    val nodeOneOutputEdge = Node("", mClass, Set(oneOutputEdge), Set(), Map.empty)
    rule.isValid(nodeOneOutputEdge).get should be(true)

    val noOutputEdges = ToEdges(outputType, Set())
    val nodeNoOutputEdges = Node("", mClass, Set(noOutputEdges), Set(), Map.empty)
    rule.isValid(nodeNoOutputEdges).get should be(true)
  }

  it should "return false on nodes of type nodeType having more than 2 output edges of type outputType" in {
    val outputType = MReference("outputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val threeOutputEdges = ToEdges(outputType, Set(outputType.name, outputType.name, outputType.name))
    val nodeThreeOutputEdges = Node("", mClass, Set(threeOutputEdges), Set(), Map.empty)
    rule.isValid(nodeThreeOutputEdges).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" toEdges "outputType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val outputMLinkDef = MReferenceLinkDef(mReference.name, 7, 0, deleteIfLower = false)

    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set(outputMLinkDef), Set[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeOutputsUpperBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeOutputsUpperBound =>
        rule.nodeType should be("class")
        rule.outputType should be("reference")
        rule.upperBound should be(7)
      case _ => fail
    }

  }
}
