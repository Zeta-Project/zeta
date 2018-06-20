package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributeEnumTypes(val nodeType: String, val attributeType: String, val enumName: String) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in nodes of type $nodeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in node $nodeType which are not of type enum $enumName."

  override def isValid(node: NodeInstance): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: NodeInstance): Boolean = node.attributeValues.get(attributeType) match {
    case None => true
    case Some(attributes) =>
      val enumType = AttributeType.EnumType(enumName)
      attributes.forall(at => at.attributeType == enumType)
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfEnumType "$enumName""""
}

object NodeAttributeEnumTypes extends GeneratorRule {

  override def generateFor(concept: Concept): Seq[DslRule] = Util.inheritAttributes(concept.classes)
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>

      acc ++ currentClass.attributes.flatMap(att => att.typ match {
        case attType: MEnum => Some(new NodeAttributeEnumTypes(currentClass.name, att.name, attType.name))
        case _ => None
      })
    }

}
