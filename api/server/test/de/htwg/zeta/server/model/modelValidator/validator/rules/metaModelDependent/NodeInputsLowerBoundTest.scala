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

class NodeInputsLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeInputsLowerBound("nodeType", "inputType", 2)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType having 2 or more input edges of type inputType" in {

    val inputType = MReference("inputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val twoInputEdges = EdgeLink(inputType.name, Seq("", ""))
    val nodeTwoInputEdge = emptyNode.copy(inputs = Seq(twoInputEdges))
    rule.isValid(nodeTwoInputEdge).get should be(true)

    val threeInputEdges = EdgeLink(inputType.name, Seq("", "", ""))
    val nodeThreeInputEdge = emptyNode.copy(inputs = Seq(threeInputEdges))
    rule.isValid(nodeThreeInputEdge).get should be(true)
  }

  it should "return false on nodes of type nodeType having less than 2 input edges of type inputType" in {

    val inputType = MReference("inputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val noInputEdges = EdgeLink(inputType.name, Seq())
    val nodeNoInputEdges = emptyNode.copy(inputs = Seq(noInputEdges))
    rule.isValid(nodeNoInputEdges).get should be(false)

    val oneInputEdge = EdgeLink(inputType.name, Seq(""))
    val nodeOneInputEdge = emptyNode.copy(inputs = Seq(oneInputEdge))
    rule.isValid(nodeOneInputEdge).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Inputs ofNodes "nodeType" toEdges "inputType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val inputMLinkDef = MReferenceLinkDef(mReference.name, -1, 5, deleteIfLower = false)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq(inputMLinkDef), Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeInputsLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeInputsLowerBound =>
        rule.nodeType should be("class")
        rule.inputType should be("reference")
        rule.lowerBound should be(5)
      case _ => fail
    }

  }

}
