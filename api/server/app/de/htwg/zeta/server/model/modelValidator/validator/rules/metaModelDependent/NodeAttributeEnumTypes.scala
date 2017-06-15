package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributeEnumTypes(val nodeType: String, val attributeType: String, val enumName: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in nodes of type $nodeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in node $nodeType which are not of type enum $enumName."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.get(attributeType) match {
    case None => true
    case Some(attribute) => attribute.headOption match {
      case None => true
      case Some(head) => head match {
        case _: EnumSymbol => attribute.collect { case v: EnumSymbol => v }.forall(_.enumName == enumName)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfEnumType "$enumName""""
}

object NodeAttributeEnumTypes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>

      acc ++ currentClass.attributes.flatMap(att => att.`type` match {
        case attType: MEnum => Some(new NodeAttributeEnumTypes(currentClass.name, att.name, attType.name))
        case _ => None
      })
    }

}
