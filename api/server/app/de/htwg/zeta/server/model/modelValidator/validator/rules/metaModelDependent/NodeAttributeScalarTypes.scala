package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributeScalarTypes(val nodeType: String, val attributeType: String, val attributeDataType: AttributeType) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Attributes of type $attributeType in nodes of type $nodeType must be of data type ${attributeDataType.asString}."
  override val possibleFix: String =
    s"Remove attribute values of attribute $attributeType in node $nodeType which are not of data type ${attributeDataType.asString}."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = {

    def handleStrings(values: StringValue): Boolean = values.attributeType == attributeDataType

    def handleBooleans(values: BoolValue): Boolean = values.attributeType == attributeDataType

    def handleInts(values: IntValue): Boolean = values.attributeType == attributeDataType

    def handleDoubles(values: DoubleValue): Boolean = values.attributeType == attributeDataType

    node.attributeValues.get(attributeType) match {
      case None => true
      case Some(attribute) => attribute match {
        case value: StringValue => handleStrings(value)
        case value: BoolValue => handleBooleans(value)
        case value: IntValue => handleInts(value)
        case value: DoubleValue => handleDoubles(value)
        case _ => true

      }
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfScalarType "${attributeDataType.asString}""""
}

object NodeAttributeScalarTypes extends GeneratorRule {

  override def generateFor(metaModel: Concept): Seq[DslRule] = Util.inheritAttributes(metaModel.classes)
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes
        .filter(att => Seq(StringType, IntType, BoolType, DoubleType).contains(att.typ))
        .map(att => new NodeAttributeScalarTypes(currentClass.name, att.name, att.typ))
    }

}
