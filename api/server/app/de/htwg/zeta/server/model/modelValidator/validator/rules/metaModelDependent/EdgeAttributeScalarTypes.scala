package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributeScalarTypes(val edgeType: String, val attributeType: String, val attributeDataType: AttributeType) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"""Attributes of type $attributeType in edges of type $edgeType must be of data type
      |${attributeDataType.asString}.""".stripMargin
  override val possibleFix: String =
    s"""Remove attribute values of attribute $attributeType in edge $edgeType which are not of data type
      |${attributeDataType.asString}.""".stripMargin

  override def isValid(edge: EdgeInstance): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: EdgeInstance): Boolean = {

    // this is nonsense. It wont be completely replaced as it is part of a recent master thesis.
    def handleAttributeValues(list: List[AttributeValue]): Boolean = list.forall(value => value.attributeType == attributeDataType)

    edge.attributeValues.get(attributeType) match {
      case None => true
      case Some(values) => handleAttributeValues(values)
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfScalarType "${attributeDataType.asString}""""
}

object EdgeAttributeScalarTypes extends GeneratorRule {
  override def generateFor(metaModel: Concept): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes
        .filter(att => Seq(StringType, IntType, BoolType, DoubleType).contains(att.typ))
        .map(att => new EdgeAttributeScalarTypes(currentReference.name, att.name, att.typ))
    }
}
