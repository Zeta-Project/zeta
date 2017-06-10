package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.model.elements.Edge

class EdgeAttributeEnumTypes(edgeType: String, attributeType: String, enumName: String) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in edges of type $edgeType must be of type enum $enumName."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in edge $edgeType which are not of type enum $enumName."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.find(_.name == attributeType) match {
    case None => true
    case Some(attribute) => attribute.value.headOption match {
      case None => true
      case Some(head) => head match {
        case _: EnumSymbol => attribute.value.collect { case v: EnumSymbol => v }.forall(_.enumName == enumName)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfEnumType "$enumName""""
}

object EdgeAttributeEnumTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>

      acc ++ currentReference.attributes.flatMap(att => att.`type` match {
        case attType: MEnum => Some(new EdgeAttributeEnumTypes(currentReference.name, att.name, attType.name))
        case _ => None
      })
    }
}
