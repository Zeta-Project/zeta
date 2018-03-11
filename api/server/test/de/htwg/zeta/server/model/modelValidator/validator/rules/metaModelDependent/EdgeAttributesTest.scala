package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )

  val emptyEdge: Edge = Edge.empty("", mReference.name, "", "")

  val rule = new EdgeAttributes("reference", Seq("stringAttribute", "boolAttribute"))

  "the rule" should "be true for valid edge" in {
    val attributes: Map[String, AttributeValue] = Map(
      "stringAttribute" -> StringValue("test"),
      "boolAttribute" -> BoolValue(true)
    )
    val edge = emptyEdge.copy(attributeValues = attributes)

    rule.isValid(edge).get should be(true)
  }

  it should "be false for invalid edges" in {
    val attributes: Map[String, AttributeValue] = Map(
      "stringAttribute" -> StringValue("test"),
      "boolAttribute" -> BoolValue(true),
      "invalidAttribute" -> DoubleValue(1.0)
    )

    val edge = Edge.empty("", mReference.name, "", "").copy(attributeValues = attributes)

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {

    val attributes: Map[String, AttributeValue] = Map(
      "stringAttribute" -> StringValue("test"),
      "boolAttribute" -> BoolValue(true)
    )

    val edge = emptyEdge.copy(referenceName = "nonMatchingReference", attributeValues = attributes)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes inEdges "reference" areOfTypes Seq("stringAttribute", "boolAttribute")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false)
    val attribute2 = MAttribute("attributeName2", globalUnique = false, localUnique = false, StringType, IntValue(0), constant = false, singleAssignment = false,
      "", ordered = false, transient = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq[MAttribute]
      (attribute, attribute2), Seq.empty)
    val metaModel = Concept.empty.copy(references = Seq(reference))
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
