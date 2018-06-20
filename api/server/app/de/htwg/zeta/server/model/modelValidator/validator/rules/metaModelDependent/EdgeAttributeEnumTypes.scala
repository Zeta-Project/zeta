package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributeEnumTypes(val edgeType: String, val attributeType: String, val enumName: String) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in edges of type $edgeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in edge $edgeType which are not of type enum $enumName."

  override def isValid(edge: EdgeInstance): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: EdgeInstance): Boolean = edge.attributeValues.get(attributeType) match {
    case None => true
    case Some(attributes) =>
      val enumType = AttributeType.EnumType(enumName)
      attributes.forall(at => at.attributeType == enumType)
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfEnumType "$enumName""""
}

object EdgeAttributeEnumTypes extends GeneratorRule {
  override def generateFor(metaModel: Concept): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>

      acc ++ currentReference.attributes.flatMap(att => att.typ match {
        case attType: MEnum => Some(new EdgeAttributeEnumTypes(currentReference.name, att.name, attType.name))
        case _ => None
      })
    }
}
