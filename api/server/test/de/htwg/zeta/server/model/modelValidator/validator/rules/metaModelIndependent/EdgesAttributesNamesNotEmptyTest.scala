package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new EdgesAttributesNamesNotEmpty
  val mReference = MReference("edgeType", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "", Seq.empty,
    Seq.empty)
  val emptyEdge: Edge = Edge.empty("", mReference.name, "", "")

  "isValid" should "return true on non-empty attribute names" in {
    val attribute: Map[String, AttributeValue] = Map("attributeName1" -> StringValue(""))
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be (true)
  }

  it should "return false on empty attribute names" in {
    val attribute: Map[String, AttributeValue] = Map("" -> StringValue(""))
    val edge = emptyEdge.copy(attributeValues = attribute)
    rule.isValid(edge).get should be (false)
  }

}
