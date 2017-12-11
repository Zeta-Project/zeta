package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesNoAttributesTest extends FlatSpec with Matchers {

  val rule = new NodesNoAttributes("nodeType")
  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)

  "isValid" should "return true on nodes of type nodeType with no attributes" in {
    rule.isValid(emptyNode).get should be(true)
  }

  it should "return false on nodes of type nodeType with attributes" in {
    val attribute: Map[String, AttributeValue] = Map("attributeType" -> StringValue(""))
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(false)
  }

  it should "return true on nodes of type nodeType with empty attribute values" in {
    val attribute: Map[String, AttributeValue] = Map.empty
    val node = emptyNode.copy(attributeValues = attribute)
    rule.isValid(node).get should be(true)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Nodes ofType "nodeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodesNoAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodesNoAttributes =>
        rule.nodeType should be("class")
      case _ => fail
    }

  }

}
