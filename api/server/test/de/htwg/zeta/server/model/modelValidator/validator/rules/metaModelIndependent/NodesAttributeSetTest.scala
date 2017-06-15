package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributeSetTest extends FlatSpec with Matchers {
  val rule = new NodesAttributeSet
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())


  "isValid" should "return true on valid nodes attribute sets" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "attributeName1" -> Set(),
      "attributeName2" -> Set()
    )
    val node = Node("", mClass, Set(), Set(), attributes)
    rule.isValid(node).get should be(true)
  }


  it should "return false on invalid edges attribute sets" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "duplicateAttributeName" -> Set(),
      "duplicateAttributeName" -> Set()
    )
    val node = Node("", mClass, Set(), Set(), attributes)
    rule.isValid(node).get should be(false)
  }
}
