package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.{EnumSymbol, MEnum}
import models.modelDefinitions.model.elements.Node

class D25_NodeAttributeEnumTypes(nodeType: String, attributeType: String, enumName: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in nodes of type $nodeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in node $nodeType which are not of type enum $enumName."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.find(_.name == attributeType) match {
    case None => true
    case Some(attribute) => attribute.value.headOption match {
      case None => true
      case Some(head) => head match {
        case _: EnumSymbol => attribute.value.collect { case v: EnumSymbol => v }.forall(_.attributeType.name == enumName)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfEnumType $enumName"""
}

object D25_NodeAttributeEnumTypes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>

      acc ++ currentClass.attributes.flatMap(att => att.`type` match {
        case attType: MEnum => Some(new D25_NodeAttributeEnumTypes(currentClass.name, att.name, attType.name))
        case _ => None
      })
    }

}
