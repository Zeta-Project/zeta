package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgesAttributesNamesNotEmptyTest extends AnyFlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  private val emptyString = ""

  val mReference = MReference(
    "edgeType",
    emptyString,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    emptyString,
    emptyString,
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    Seq.empty,
    Seq.empty
  )
  val emptyEdge: EdgeInstance = EdgeInstance.empty(emptyString, mReference.name, emptyString, emptyString)

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, List[AttributeValue]] = Map("attributeName1" -> List(StringValue(emptyString)))
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, List[AttributeValue]] = Map(emptyString -> List(StringValue(emptyString)))
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be(false)
  }

}
