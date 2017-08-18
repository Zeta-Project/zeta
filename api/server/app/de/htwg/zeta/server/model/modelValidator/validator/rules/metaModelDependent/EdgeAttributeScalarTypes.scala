package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
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

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Boolean = values.collect { case v: StringValue => v }.forall(_.attributeType == attributeDataType)
    def handleBooleans(values: Seq[AttributeValue]): Boolean = values.collect { case v: BoolValue => v }.forall(_.attributeType == attributeDataType)
    def handleInts(values: Seq[AttributeValue]): Boolean = values.collect { case v: IntValue => v }.forall(_.attributeType == attributeDataType)
    def handleDoubles(values: Seq[AttributeValue]): Boolean = values.collect { case v: DoubleValue => v }.forall(_.attributeType == attributeDataType)

    edge.attributeValues.get(attributeType) match {
      case None => true
      case Some(attribute) => attribute.headOption match {
        case None => true
        case Some(head) => head match {
          case _: StringValue => handleStrings(attribute)
          case _: BoolValue => handleBooleans(attribute)
          case _: IntValue => handleInts(attribute)
          case _: DoubleValue => handleDoubles(attribute)
          case _ => true
        }
      }
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfScalarType "${attributeDataType.asString}""""
}

object EdgeAttributeScalarTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes
        .filter(att => Seq(StringType, IntType, BoolType, DoubleType).contains(att.typ))
        .map(att => new EdgeAttributeScalarTypes(currentReference.name, att.name, att.typ))
    }
}
