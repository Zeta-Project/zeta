package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new NodesAttributesNamesNotEmpty
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeName1" -> Set())
    val node = Node("", mClass, Set(), Set(), attribute)
    rule.isValid(node).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("" -> Set())
    val node = Node("", mClass, Set(), Set(), attribute)
    rule.isValid(node).get should be(false)
  }

}
