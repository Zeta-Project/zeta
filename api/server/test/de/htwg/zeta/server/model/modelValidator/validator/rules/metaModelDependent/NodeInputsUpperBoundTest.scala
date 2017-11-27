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

class NodeInputsUpperBoundTest extends FlatSpec with Matchers {

  val rule = new NodeInputsUpperBound("nodeType", "inputType", 2)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType having 2 or less input edges of type inputType" in {
    val inputType = MReference("inputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val twoInputEdges = EdgeLink(inputType.name, Seq("", ""))
    val nodeTwoInputEdge = emptyNode.copy(inputEdgeNames = Seq(twoInputEdges))
    rule.isValid(nodeTwoInputEdge).get should be(true)

    val oneInputEdge = EdgeLink(inputType.name, Seq(""))
    val nodeOneInputEdge = emptyNode.copy(inputEdgeNames = Seq(oneInputEdge))
    rule.isValid(nodeOneInputEdge).get should be(true)

    val noInputEdges = EdgeLink(inputType.name, Seq())
    val nodeNoInputEdges = emptyNode.copy(inputEdgeNames = Seq(noInputEdges))
    rule.isValid(nodeNoInputEdges).get should be(true)
  }

  it should "return false on nodes of type nodeType having more than 2 input edges of type inputType" in {
    val inputType = MReference("inputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val threeInputEdges = EdgeLink(inputType.name, Seq("", "", ""))
    val nodeThreeInputEdges = emptyNode.copy(inputEdgeNames = Seq(threeInputEdges))
    rule.isValid(nodeThreeInputEdges).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Inputs ofNodes "nodeType" toEdges "inputType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val inputMLinkDef = MReferenceLinkDef(mReference.name, 7, 0, deleteIfLower = false)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq(inputMLinkDef), Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeInputsUpperBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeInputsUpperBound =>
        rule.nodeType should be("class")
        rule.inputType should be("reference")
        rule.upperBound should be(7)
      case _ => fail
    }

  }

}
