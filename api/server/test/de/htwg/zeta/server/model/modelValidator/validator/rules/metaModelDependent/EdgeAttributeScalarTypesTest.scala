package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgeAttributeScalarTypesTest extends AnyFlatSpec with Matchers {

  private val emptyString = ""
  private val referenceLiteral = "reference"
  val mReference: MReference = MReference.empty(referenceLiteral, emptyString, emptyString)
  val emptyEdge: EdgeInstance = EdgeInstance.empty(emptyString, mReference.name, emptyString, emptyString)
  val rule = new EdgeAttributeScalarTypes(referenceLiteral, "attributeType", StringType)

  "the rule" should "be true for valid edges" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("value")))
    val edge = emptyEdge.copy(attributeValues = attribute)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(IntValue(42)))
    val edge = emptyEdge.copy(attributeValues = attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "return None for non-matching edges" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("value")))
    val edge = emptyEdge.copy(referenceName = "differentMReference", attributeValues = attribute)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges """" + referenceLiteral + """" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Seq("enumValue1", "enumValue2"))

    val enumAttribute = MAttribute(
      "attributeName",
      globalUnique = false,
      localUnique = false,
      enumType.typ,
      enumType.values.head,
      constant = false,
      singleAssignment = false,
      emptyString,
      ordered = false,
      transient = false
    )
    val scalarAttribute = MAttribute(
      "attributeName2",
      globalUnique = false,
      localUnique = false,
      StringType,
      StringValue(emptyString),
      constant = false,
      singleAssignment = false,
      emptyString,
      ordered = false,
      transient = false
    )
    val reference = MReference(
      referenceLiteral,
      emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      emptyString,
      emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](enumAttribute, scalarAttribute),
      Seq.empty
    )
    val metaModel = Concept.empty.copy(references = Seq(reference))
    val result = EdgeAttributeScalarTypes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributeScalarTypes =>
        rule.edgeType should be(referenceLiteral)
        rule.attributeType should be("attributeName2")
        rule.attributeDataType should be(StringType)
      case _ => fail
    }
  }

}
