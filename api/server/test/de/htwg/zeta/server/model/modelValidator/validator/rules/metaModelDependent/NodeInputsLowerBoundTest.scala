package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.ToEdges
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeInputsLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeInputsLowerBound("nodeType", "inputType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType having 2 or more input edges of type inputType" in {

    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val twoInputEdges = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeTwoInputEdge = Node.apply2("", mClass, Seq(), Seq(twoInputEdges), Seq())
    rule.isValid(nodeTwoInputEdge).get should be (true)

    val threeInputEdges = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq()),
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeThreeInputEdge = Node.apply2("", mClass, Seq(), Seq(threeInputEdges), Seq())
    rule.isValid(nodeThreeInputEdge).get should be (true)
  }

  it should "return false on nodes of type nodeType having less than 2 input edges of type inputType" in {

    val inputType = MReference("inputType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val noInputEdges = ToEdges(inputType, Seq())
    val nodeNoInputEdges = Node.apply2("", mClass, Seq(), Seq(noInputEdges), Seq())
    rule.isValid(nodeNoInputEdges).get should be (false)

    val oneInputEdge = ToEdges(inputType, Seq(
      Edge.apply2("", inputType, Seq(), Seq(), Seq())
    ))
    val nodeOneInputEdge = Node.apply2("", mClass, Seq(), Seq(oneInputEdge), Seq())
    rule.isValid(nodeOneInputEdge).get should be (false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Inputs ofNodes "nodeType" toEdges "inputType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val inputMLinkDef = MLinkDef(mReference, -1, 5, deleteIfLower = false)

    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](inputMLinkDef), Seq[MLinkDef](), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
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
