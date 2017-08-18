package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeScalarTypesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new EdgeAttributeScalarTypes("reference", "attributeType", StringType)

  "the rule" should "be true for valid edges" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("value")))
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), attribute)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(IntValue(42)))
    val edge = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), attribute)

    rule.isValid(edge).get should be(false)
  }

  it should "return None for non-matching edges" in {
    val differentMReference = MReference(
      "differentMReference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("value")))
    val edge = Edge(UUID.randomUUID(), differentMReference.name, Seq(), Seq(), attribute)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfScalarType "String"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Seq("enumValue1", "enumValue2"))

    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumType.values.head, constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false,
      singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq[MAttribute]
      (enumAttribute, scalarAttribute), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
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
