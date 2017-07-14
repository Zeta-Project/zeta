package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLocalUniqueTest extends FlatSpec with Matchers {

  val mClass = MClass("nodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Map.empty)
  val rule = new NodeAttributesLocalUnique("nodeType", "attributeType")

  "isValid" should "return true on valid nodes" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(MString("value1"), MString("value2"), MString("value3")))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(true)
  }

  it should "return false on invalid nodes" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(MString("duplicateValue"), MString("value"), MString("duplicateValue")))
    val node = Node("", mClass, Seq(), Seq(), attribute)

    rule.isValid(node).get should be(false)
  }

  it should "return None for non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Map.empty)
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" areLocalUnique ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val localUniqueAttribute = MAttribute("attributeName", globalUnique = false, localUnique = true, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val nonLocalUniqueAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]
      (nonLocalUniqueAttribute, localUniqueAttribute), Map.empty)
    val metaModel = TestUtil.classesToMetaModel(Seq(mClass))
    val result = NodeAttributesLocalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributesLocalUnique =>
        rule.nodeType should be("class")
        rule.attributeType should be("attributeName")
      case _ => fail
    }

  }
}
