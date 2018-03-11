package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoOutputsTest extends FlatSpec with Matchers {
  val rule = new NodesNoOutputs("nodeType")
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with no outputs" in {
    rule.isValid(emptyNode).get should be(true)
  }

  it should "return false on nodes of type nodeType with outputs" in {
    val output = MReference("", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node = emptyNode.copy(outputEdgeNames = Seq(output.name))
    rule.isValid(node).get should be(false)
  }

  it should "return true on nodes of type nodeType with empty output list" in {
    val output = MReference("", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq(), Seq.empty)
    val node = emptyNode.copy(outputEdgeNames = Seq(output.name))
    rule.isValid(node).get should be(true)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Nodes ofType "nodeType" haveNoOutputs ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodesNoOutputs.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodesNoOutputs =>
        rule.nodeType should be("class")
      case _ => fail
    }

  }
}
