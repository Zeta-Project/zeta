package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesUpperBound(val edgeType: String, val attributeType: String, val upperBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have a maximum of $upperBound attributes of type $attributeType."
  override val possibleFix: String =
    s"Remove attributes of type $attributeType from edges of type $edgeType until there are a maximum of $upperBound attributes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = if (upperBound == -1) true else edge.attributes.get(attributeType) match {
    case Some(attribute) => attribute.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" haveUpperBound $upperBound"""
}

object EdgeAttributesUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.map(attr => new EdgeAttributesUpperBound(currentReference.name, attr.name, attr.upperBound))
    }
}
