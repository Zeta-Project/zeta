package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesTest extends FlatSpec with Matchers {

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

  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)

  val rule = new EdgeAttributes("reference", Seq("stringAttribute", "boolAttribute"))

  "the rule" should "be true for valid edge" in {
    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "stringAttribute" -> Seq(StringValue("test")),
      "boolAttribute" -> Seq(BoolValue(true))
    )
    val edge = emptyEdge.copy(attributeValues = attributes)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "stringAttribute" -> Seq(StringValue("test")),
      "boolAttribute" -> Seq(BoolValue(true)),
      "invalidAttribute" -> Seq(DoubleValue(1.0))
    )

    val edge = Edge.empty("", mReference.name, Seq(), Seq()).copy(attributeValues = attributes)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {

    val nonMatchingReference = MReference(
      "nonMatchingReference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )

    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "stringAttribute" -> Seq(StringValue("test")),
      "boolAttribute" -> Seq(BoolValue(true))
    )

    val edge = emptyEdge.copy(attributeValues = attributes)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inEdges "reference" areOfTypes Seq("stringAttribute", "boolAttribute")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 0)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, IntValue(0), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, -1, 0)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq[MAttribute]
      (attribute, attribute2), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributes =>
        rule.edgeType should be("reference")
        rule.attributeTypes should be(Seq("attributeName", "attributeName2"))
      case _ => fail
    }
  }

}
