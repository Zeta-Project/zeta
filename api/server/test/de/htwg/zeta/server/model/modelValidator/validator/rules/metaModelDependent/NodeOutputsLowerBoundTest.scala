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

class NodeOutputsLowerBoundTest extends FlatSpec with Matchers {
  val rule = new NodeOutputsLowerBound("nodeType", "outputType", 2)
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType having 2 or more output edges of type outputType" in {

    val outputType = MReference("outputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val twoOutputEdges = EdgeLink(outputType.name, Seq("", ""))
    val nodeTwoOutputEdges = emptyNode.copy(outputs = Seq(twoOutputEdges))
    rule.isValid(nodeTwoOutputEdges).get should be(true)

    val threeOutputEdges = EdgeLink(outputType.name, Seq("", "", ""))
    val nodeThreeOutputEdges = emptyNode.copy(outputs = Seq(threeOutputEdges))
    rule.isValid(nodeThreeOutputEdges).get should be(true)
  }

  it should "return false on nodes of type nodeType having less than 2 output edges of type outputType" in {

    val outputType = MReference("outputType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val noOutputEdges = EdgeLink(outputType.name, Seq())
    val nodeNoOutputEdges = emptyNode.copy(outputs = Seq(noOutputEdges))
    rule.isValid(nodeNoOutputEdges).get should be(false)

    val oneOutputEdge = EdgeLink(outputType.name, Seq(""))
    val nodeOneOutputEdge = emptyNode.copy(outputs = Seq(oneOutputEdge))
    rule.isValid(nodeOneOutputEdge).get should be(false)
  }

  it should "return None on non-matching nodes" in {
  val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Outputs ofNodes "nodeType" toEdges "outputType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq(), Seq.empty)
    val outputMLinkDef = MReferenceLinkDef(mReference.name, -1, 5, deleteIfLower = false)

    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq(outputMLinkDef), Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeOutputsLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeOutputsLowerBound =>
        rule.nodeType should be("class")
        rule.outputType should be("reference")
        rule.lowerBound should be(5)
      case _ => fail
    }

  }
}
