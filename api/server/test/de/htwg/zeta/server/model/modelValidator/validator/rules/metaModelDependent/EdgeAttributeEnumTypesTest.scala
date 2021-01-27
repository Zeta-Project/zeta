package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgeAttributeEnumTypesTest extends AnyFlatSpec with Matchers {

  private val emptyString = ""
  val mReference: MReference = MReference.empty("reference", emptyString, emptyString)
  val emptyEdge: EdgeInstance = EdgeInstance.empty(emptyString, mReference.name, emptyString, emptyString)
  val rule = new EdgeAttributeEnumTypes("reference", "attributeType", "enumName")


  "the rule" should "be true for valid edges" in {
    val mEnum = MEnum(name = "enumName", valueNames = Seq())
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(EnumValue("enumName", mEnum.name)))
    val edge = emptyEdge

    rule.isValid(edge).get should be(true)
  }

  it should "return None for non-matching edge" in {
    val differentMReference = MReference(
      "differentMReference",
      emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      emptyString,
      emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = EdgeInstance.empty(emptyString, differentMReference.name, emptyString, emptyString)

    rule.isValid(edge) should be(None)
  }

  it should "be false for invalid edges" in {
    val differentEnum = MEnum(name = "differentEnumName", valueNames = Seq())
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(EnumValue("differentEnumName", differentEnum.name)))
    val edge = EdgeInstance.empty(emptyString, mReference.name, emptyString, emptyString).copy(attributeValues = attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {
    val differentReference = MReference(
      "differentRef",
      emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      emptyString,
      emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = EdgeInstance.empty(emptyString, differentReference.name, emptyString, emptyString)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfEnumType "enumName"""")
  }

  "generateFor" should "generate this rule from the meta model" ignore {
    val enum = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute(
      "attributeName",
      globalUnique = false,
      localUnique = false,
      enum.typ,
      enum.values.head,
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
      "reference",
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
    val result = EdgeAttributeEnumTypes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributeEnumTypes =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
        rule.enumName should be("enumName")
      case _ => fail
    }

  }

}
