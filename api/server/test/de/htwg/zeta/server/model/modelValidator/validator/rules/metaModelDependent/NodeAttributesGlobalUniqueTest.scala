package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesGlobalUniqueTest extends FlatSpec with Matchers {

  val mClass1 = MClass("nodeType1", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
  val mClass2 = MClass("nodeType2", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
  val rule = new NodeAttributesGlobalUnique(Seq("nodeType1", "nodeType2"), "attributeType")

  "check" should "return success validation results on correct attributes" in {

    val attribute1: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value1")))
    val node1 = Node("", mClass1, Set(), Set(), attribute1)

    val attribute2: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value2")))
    val node2 = Node("", mClass1, Set(), Set(), attribute2)

    val attribute3: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value3")))
    val node3 = Node("", mClass2, Set(), Set(), attribute3)

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be(3)
    results.forall(_.valid) should be(true)
  }

  it should "return failure validation results on invalid attributes" in {
    val attribute1: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("duplicateValue")))
    val node1 = Node("", mClass1, Set(), Set(), attribute1)

    val attribute2: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value")))
    val node2 = Node("", mClass1, Set(), Set(), attribute2)

    val attribute3: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("duplicateValue")))
    val node3 = Node("", mClass2, Set(), Set(), attribute3)

    val results = rule.check(Seq(node1, node2, node3))

    results.size should be(3)
    results.head.valid should be(false)
    results(1).valid should be(true)
    results(2).valid should be(false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes Set("nodeType1", "nodeType2") areGlobalUnique ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val globalUniqueAttribute = MAttribute("attributeName", globalUnique = true, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val nonGlobalUniqueAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val mClass = MClass("class", abstractness = false, superTypeNames = Set.empty, Set.empty, Set.empty, Set[MAttribute]
      (nonGlobalUniqueAttribute, globalUniqueAttribute))
    val metaModel = TestUtil.classesToMetaModel(Set(mClass))
    val result = NodeAttributesGlobalUnique.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: NodeAttributesGlobalUnique =>
        rule.nodeTypes should be(Set("class"))
        rule.attributeType should be("attributeName")
      case _ => fail
    }

  }

}
