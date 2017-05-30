package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Node

class NodeAttributesLocalUnique(nodeType: String, attributeType: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Node of type $nodeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MString => v }.map(_.value)
  def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MBool => v }.map(_.value.toString)
  def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MInt => v }.map(_.value.toString)
  def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MDouble => v }.map(_.value.toString)
  def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)

  def rule(node: Node): Boolean = {
    node.attributes.find(_.name == attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Seq[String] = attribute.value.headOption match {
          case None => Seq()
          case Some(_: MString) => handleStrings(attribute.value)
          case Some(_: MBool) => handleBooleans(attribute.value)
          case Some(_: MInt) => handleInts(attribute.value)
          case Some(_: MDouble) => handleDoubles(attribute.value)
          case Some(_: EnumSymbol) => handleEnums(attribute.value)
        }

        if (attributeValues.isEmpty) true else attributeValues.distinct.size == attribute.value.size
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
