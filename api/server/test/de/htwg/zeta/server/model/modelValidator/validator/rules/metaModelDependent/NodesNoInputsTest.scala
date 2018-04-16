package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoInputsTest extends FlatSpec with Matchers {

  private val emptyString = ""
  val rule = new NodesNoInputs("nodeType")
  val mClass = MClass("nodeType", emptyString, abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: NodeInstance = NodeInstance.empty(emptyString, mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with no inputs" in {
    rule.isValid(emptyNode).get should be(true)
  }

  it should "return false on nodes of type nodeType with inputs" in {
    val input = MReference(
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
    val node = emptyNode.copy(inputEdgeNames = Seq(input.name))
    rule.isValid(node).get should be(false)
  }

  it should "return true on nodes of type nodeType with empty input list" ignore {
    val input = MReference(
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
    val node = emptyNode.copy(inputEdgeNames = Seq(input.name))
    rule.isValid(node).get should be(true)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Nodes ofType "nodeType" haveNoInputs ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", emptyString, abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodesNoInputs.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodesNoInputs =>
        rule.nodeType should be("class")
      case _ => fail
    }

  }

}
