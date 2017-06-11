package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.ScalarBoolValue
import models.modelDefinitions.metaModel.elements.ScalarDoubleValue
import models.modelDefinitions.metaModel.elements.ScalarIntValue
import models.modelDefinitions.metaModel.elements.ScalarStringValue
import models.modelDefinitions.model.elements.Node

class NodeAttributesLocalUnique(nodeType: String, attributeType: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Node of type $nodeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarStringValue => v }.map(_.value)
  def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarBoolValue => v }.map(_.value.toString)
  def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarIntValue => v }.map(_.value.toString)
  def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarDoubleValue => v }.map(_.value.toString)
  def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)

  def rule(node: Node): Boolean = {
    node.attributes.get(attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Seq[String] = attribute.headOption match {
          case None => Seq()
          case Some(_: ScalarStringValue) => handleStrings(attribute)
          case Some(_: ScalarBoolValue) => handleBooleans(attribute)
          case Some(_: ScalarIntValue) => handleInts(attribute)
          case Some(_: ScalarDoubleValue) => handleDoubles(attribute)
          case Some(_: EnumSymbol) => handleEnums(attribute)
        }

        if (attributeValues.isEmpty) true else attributeValues.distinct.size == attribute.size
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" areLocalUnique ()"""
}

object NodeAttributesLocalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes.filter(_.localUnique).map(attr => new NodeAttributesLocalUnique(currentClass.name, attr.name))
    }
}
