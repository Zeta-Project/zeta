package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val emptyNode: Node = Node.empty("", mClass.name, Seq.empty, Seq.empty)
  val rule = new NodeAttributes("nodeType", Seq("att1", "att2"))

  "isValid" should "return true for valid nodes" in {
    val attributes: Map[String, AttributeValue] = Map(
      "att1" -> StringValue(""),
      "att2" -> BoolValue(false)
    )
    val node = emptyNode.copy(attributeValues = attributes)

    rule.isValid(node).get should be(true)
  }

  it should "return false for invalid nodes" in {
    val attributes: Map[String, AttributeValue] = Map(
      "att1" -> StringValue(""),
      "att2" -> BoolValue(false),
      "att3" -> IntValue(0)
    )
    val node = emptyNode.copy(attributeValues = attributes)

    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val node = emptyNode.copy(className = "differentNodeType")
    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inNodes "nodeType" areOfTypes Seq("att1", "att2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute1 = MAttribute("attributeName1", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](attribute1,
      attribute2), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass))
    val result = NodeAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributes =>
        rule.nodeType should be("class")
        rule.attributeTypes should be(Seq("attributeName1", "attributeName2"))
      case _ => fail
    }

  }

}
