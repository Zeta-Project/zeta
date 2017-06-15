package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeScalarTypesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeAttributeScalarTypes("reference", "attributeType", StringType)

  "the rule" should "be true for valid edges" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value")))
    val edge = Edge("edgeId", mReference, Set(), Set(), attribute)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MInt(42)))
    val edge = Edge("edgeId", mReference, Set(), Set(), attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "return None for non-matching edges" in {
    val differentMReference = MReference(
      "differentMReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("value")))
    val edge = Edge("edgeId", differentMReference, Set(), Set(), attribute)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Set("enumValue1", "enumValue2"))

    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumType.symbols.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, MString(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set[MAttribute]
      (enumAttribute, scalarAttribute))
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeAttributeScalarTypes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributeScalarTypes =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName2")
        rule.attributeDataType should be(StringType)
      case _ => fail
    }
  }

}
