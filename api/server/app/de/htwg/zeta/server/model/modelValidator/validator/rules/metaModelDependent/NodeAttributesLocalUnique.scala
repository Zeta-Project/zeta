package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesLocalUnique(val nodeType: String, val attributeType: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Node of type $nodeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def handleStrings(values: Set[AttributeValue]): Set[String] = values.collect { case v: MString => v }.map(_.value)
  def handleBooleans(values: Set[AttributeValue]): Set[String] = values.collect { case v: MBool => v }.map(_.value.toString)
  def handleInts(values: Set[AttributeValue]): Set[String] = values.collect { case v: MInt => v }.map(_.value.toString)
  def handleDoubles(values: Set[AttributeValue]): Set[String] = values.collect { case v: MDouble => v }.map(_.value.toString)
  def handleEnums(values: Set[AttributeValue]): Set[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)

  def rule(node: Node): Boolean = {
    node.attributes.get(attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Set[String] = attribute.headOption match {
          case None => Set.empty
          case Some(_: MString) => handleStrings(attribute)
          case Some(_: MBool) => handleBooleans(attribute)
          case Some(_: MInt) => handleInts(attribute)
          case Some(_: MDouble) => handleDoubles(attribute)
          case Some(_: EnumSymbol) => handleEnums(attribute)
        }

        if (attributeValues.isEmpty) true else attributeValues.size == attribute.size
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
