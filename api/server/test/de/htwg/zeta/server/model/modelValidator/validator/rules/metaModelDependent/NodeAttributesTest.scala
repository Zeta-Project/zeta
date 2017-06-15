package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
  val rule = new NodeAttributes("nodeType", Seq("att1", "att2"))

  "isValid" should "return true for valid nodes" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "att1" -> Set(MString("")),
      "att2" -> Set(MBool(false))
    )
    val node = Node("", mClass, Set(), Set(), attributes)

    rule.isValid(node).get should be(true)
  }

  it should "return false for invalid nodes" in {
    val attributes: Map[String, Set[AttributeValue]] = Map(
      "att1" -> Set(MString("")),
      "att2" -> Set(MBool(false)),
      "att3" -> Set(MInt(0))
    )
    val node = Node("", mClass, Set(), Set(), attributes)

    rule.isValid(node).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val node = Node("", differentMClass, Set(), Set(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inNodes "nodeType" areOfTypes Set("att1", "att2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute1 = MAttribute("attributeName1", globalUnique = false, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute](attribute1,
      attribute2))
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributes =>
        rule.nodeType should be("class")
        rule.attributeTypes should be(Set("attributeName1", "attributeName2"))
      case _ => fail
    }

  }

}
