package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new NodesAttributesNamesNotEmpty
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, AttributeValue] = Map("attributeName1" -> StringValue(""))
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, AttributeValue] = Map("" -> StringValue(""))
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(false)
  }

}
