package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoAttributesTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: EdgeInstance = EdgeInstance.empty("", mReference.name, "", "")
  val rule = new EdgesNoAttributes("edgeType")

  "isValid" should "return true on edges of type edgeType with no attributes" in {
    val edge = emptyEdge
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with attributes" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeType" -> List(StringValue("att")))
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty attribute values" in {
    val attribute: Map[String, List[AttributeValue]] = Map.empty
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      "",
      "",
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentReference.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges ofType "edgeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference(
      "reference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      "",
      "",
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val metaModel = Concept.empty.copy(references = Seq(reference))
    val result = EdgesNoAttributes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgesNoAttributes =>
        rule.edgeType should be("reference")
      case _ => fail
    }
  }
}
