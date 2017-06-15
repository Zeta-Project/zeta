package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeAttributesLowerBound("edgeType", "attributeType", 2)

  "isValid" should "return true on edges with 2 or more attributes of type attributeType" in {
    val twoAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att1"), MString("att2")))
    val twoAttributesEdge = Edge("edgeId", mReference, Set(), Set(), twoAttributes)

    rule.isValid(twoAttributesEdge).get should be(true)

    val threeAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesEdge = Edge("edgeId", mReference, Set(), Set(), threeAttributes)

    rule.isValid(threeAttributesEdge).get should be(true)
  }

  it should "return false on edges with less than 2 attributes of type attributeType" in {
    val noAttributes: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set.empty)
    val noAttributesEdge = Edge("edgeId", mReference, Set(), Set(), noAttributes)
    rule.isValid(noAttributesEdge).get should be(false)

    val oneAttribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att")))
    val oneAttributeEdge = Edge("edgeId", mReference, Set(), Set(), oneAttribute)

    rule.isValid(oneAttributeEdge).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val edge = Edge("edgeId", differentReference, Set(), Set(), Map.empty)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "edgeType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, MString(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 5)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set[MAttribute]
      (attribute))
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeAttributesLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributesLowerBound =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
        rule.lowerBound should be(5)
      case _ => fail
    }
  }

}
