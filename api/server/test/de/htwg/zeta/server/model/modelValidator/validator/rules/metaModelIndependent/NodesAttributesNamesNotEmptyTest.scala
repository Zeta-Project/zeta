package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodesAttributesNamesNotEmptyTest extends AnyFlatSpec with Matchers {

  val rule = new NodesAttributesNamesNotEmpty
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: NodeInstance = NodeInstance.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeName1" -> List(StringValue("")))
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, List[AttributeValue]] = Map("" -> List(StringValue("")))
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(false)
  }

}
