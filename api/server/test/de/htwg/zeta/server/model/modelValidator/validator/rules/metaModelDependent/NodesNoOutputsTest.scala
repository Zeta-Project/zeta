package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodesNoOutputsTest extends AnyFlatSpec with Matchers {

  private val emptyString = ""
  val rule = new NodesNoOutputs("nodeType")
  val mClass = MClass("nodeType", emptyString, abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: NodeInstance = NodeInstance.empty(emptyString, mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with no outputs" in {
    rule.isValid(emptyNode).get should be(true)
  }

  it should "return false on nodes of type nodeType with outputs" in {
    val output = MReference(
      emptyString,
      emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      emptyString,
      emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq(),
      Seq.empty
    )
    val node = emptyNode.copy(outputEdgeNames = Seq(output.name))
    rule.isValid(node).get should be(false)
  }

  it should "return true on nodes of type nodeType with empty output list" ignore {
    val output = MReference(
      emptyString,
      emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      emptyString,
      emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq(),
      Seq.empty
    )
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
    val mClass = MClass("class", emptyString, abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
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
