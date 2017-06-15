package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoAttributesTest extends FlatSpec with Matchers {

  val rule = new NodesNoAttributes("nodeType")
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes of type nodeType with no attributes" in {
    val node = Node("", mClass, Set(), Set(), Map.empty)
    rule.isValid(node).get should be (true)
  }

  it should "return false on nodes of type nodeType with attributes" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("")))
    val node = Node("", mClass, Set(), Set(), attribute)
    rule.isValid(node).get should be (false)
  }

  it should "return true on nodes of type nodeType with empty attribute values" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set())
    val node = Node("", mClass, Set(), Set(), attribute)
    rule.isValid(node).get should be (true)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)
    rule.isValid(node) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Nodes ofType "nodeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodesNoAttributes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodesNoAttributes =>
        rule.nodeType should be ("class")
      case _ => fail
    }

  }

}
