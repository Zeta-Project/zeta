package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeInputsLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeInputsLowerBound("nodeType", "inputType", 2)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes of type nodeType having 2 or more input edges of type inputType" in {

    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val twoInputEdges = ToEdges(inputType, Set(inputType.name))
    val nodeTwoInputEdge = Node("", mClass, Set(), Set(twoInputEdges), Map.empty)
    rule.isValid(nodeTwoInputEdge).get should be(true)

    val threeInputEdges = ToEdges(inputType, Set(inputType.name, inputType.name, inputType.name))
    val nodeThreeInputEdge = Node("", mClass, Set(), Set(threeInputEdges), Map.empty)
    rule.isValid(nodeThreeInputEdge).get should be(true)
  }

  it should "return false on nodes of type nodeType having less than 2 input edges of type inputType" in {

    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val noInputEdges = ToEdges(inputType, Set())
    val nodeNoInputEdges = Node("", mClass, Set(), Set(noInputEdges), Map.empty)
    rule.isValid(nodeNoInputEdges).get should be(false)

    val oneInputEdge = ToEdges(inputType, Set(inputType.name))
    val nodeOneInputEdge = Node("", mClass, Set(), Set(oneInputEdge), Map.empty)
    rule.isValid(nodeOneInputEdge).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Inputs ofNodes "nodeType" toEdges "inputType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val inputMLinkDef = MReferenceLinkDef(mReference.name, -1, 5, deleteIfLower = false)

    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set(inputMLinkDef), Set.empty, Set[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeInputsLowerBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeInputsLowerBound =>
        rule.nodeType should be ("class")
        rule.inputType should be ("reference")
        rule.lowerBound should be (5)
      case _ => fail
    }

  }

}
