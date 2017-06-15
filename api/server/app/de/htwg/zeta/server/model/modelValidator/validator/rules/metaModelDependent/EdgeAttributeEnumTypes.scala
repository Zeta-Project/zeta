package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributeEnumTypes(val edgeType: String, val attributeType: String, val enumName: String) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in edges of type $edgeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in edge $edgeType which are not of type enum $enumName."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.get(attributeType) match {
    case None => true
    case Some(attribute) => attribute.headOption match {
      case None => true
      case Some(head) => head match {
        case _: EnumSymbol => attribute.collect { case v: EnumSymbol => v }.forall(_.enumName == enumName)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfEnumType "$enumName""""
}

object EdgeAttributeEnumTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>

      acc ++ currentReference.attributes.flatMap(att => att.typ match {
        case attType: MEnum => Some(new EdgeAttributeEnumTypes(currentReference.name, att.name, attType.name))
        case _ => None
      })
    }
}
