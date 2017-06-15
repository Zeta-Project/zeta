package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoInputsTest extends FlatSpec with Matchers {

  val rule = new NodesNoInputs("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no inputs" in {
    val node = Node("", mClass, Set(), Set(), Map.empty)
    rule.isValid(node).get should be(true)
  }

  it should "return false on nodes of type nodeType with inputs" in {
    val input = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val toEdge = ToEdges(input, Set(""))
    val node = Node("", mClass, Set(), Set(toEdge), Map.empty)
    rule.isValid(node).get should be(false)
  }

  it should "return true on nodes of type nodeType with empty input list" in {
    val input = MReference("", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(), Set(), Set())
    val toEdge = ToEdges(input, Set())
    val node = Node("", mClass, Set(), Set(toEdge), Map.empty)
    rule.isValid(node).get should be(true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Nodes ofType "nodeType" haveNoInputs ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodesNoInputs.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodesNoInputs =>
        rule.nodeType should be("class")
      case _ => fail
    }

  }

}
