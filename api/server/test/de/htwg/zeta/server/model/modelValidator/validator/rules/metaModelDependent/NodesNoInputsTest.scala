package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoInputsTest extends FlatSpec with Matchers {

  val rule = new NodesNoInputs("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no inputs" in {
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq())
    rule.isValid(node).get should be (true)
  }

  it should "return false on nodes of type nodeType with inputs" in {
    val input = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdge = ToEdges(input, Seq(
      Edge.apply2("", input, Seq(), Seq(), Seq())
    ))
    val node = Node.apply2("", mClass, Seq(), Seq(toEdge), Seq())
    rule.isValid(node).get should be (false)
  }

  it should "return true on nodes of type nodeType with empty input list" in {
    val input = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(), Seq(), Seq())
    val toEdge = ToEdges(input, Seq())
    val node = Node.apply2("", mClass, Seq(), Seq(toEdge), Seq())
    rule.isValid(node).get should be (true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val node = Node.apply2("", differentMClass, Seq(), Seq(), Seq())
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Nodes ofType "nodeType" haveNoInputs ()""")
  }

}
