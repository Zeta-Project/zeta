package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesUpperBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesUpperBound("nodeType", "attributeType", 2)
  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())

  "isValid" should "return true on nodes with 2 or less attributes of type attributeType" in {
    val noAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set())
    val noAttributesNode = Node("", mClass, Set(), Set(), noAttributes)

    rule.isValid(noAttributesNode).get should be(true)

    val oneAttribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att")))
    val oneAttributeNode = Node("", mClass, Set(), Set(), oneAttribute)

    rule.isValid(oneAttributeNode).get should be(true)

    val twoAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att1"), MString("att2")))
    val twoAttributesNode = Node("", mClass, Set(), Set(), twoAttributes)

    rule.isValid(twoAttributesNode).get should be(true)
  }

  it should "return false on nodes with more than 2 attributes of type attributeType" in {
    val threeAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesNode = Node("", mClass, Set(), Set(), threeAttributes)

    rule.isValid(threeAttributesNode).get should be(false)

    val fourAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att1"), MString("att2"), MString("att3"), MString("att4")))
    val fourAttributesNode = Node("", mClass, Set(), Set(), fourAttributes)
    rule.isValid(fourAttributesNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, MString(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, 7, 0)
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute](attribute))
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeAttributesUpperBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributesUpperBound =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName")
        rule.upperBound should be (7)
      case _ => fail
    }

  }

}
