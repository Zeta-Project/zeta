package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.Node

class D17_NodeAttributesLocalUnique(nodeType: String, attributeType: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Node of type $nodeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = {
    node.attributes.find(_.name == attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Seq[String] = attribute.value.headOption match {
          case None => Seq()
          case Some(_: MString) => attribute.value.collect { case v: MString => v }.map(_.value)
          case Some(_: MBool) => attribute.value.collect { case v: MBool => v }.map(_.value.toString)
          case Some(_: MInt) => attribute.value.collect { case v: MInt => v }.map(_.value.toString)
          case Some(_: MDouble) => attribute.value.collect { case v: MDouble => v }.map(_.value.toString)
          case Some(_: EnumSymbol) => attribute.value.collect { case v: EnumSymbol => v }.map(_.toString)
        }
        if (attributeValues.isEmpty) true
        else attributeValues.distinct.size == attribute.value.size
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" areLocalUnique ()"""
}

object D17_NodeAttributesLocalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes.filter(_.localUnique).map(attr => new D17_NodeAttributesLocalUnique(currentClass.name, attr.name))
    }
}
