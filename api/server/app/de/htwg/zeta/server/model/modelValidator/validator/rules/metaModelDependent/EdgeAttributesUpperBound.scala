package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class EdgeAttributesUpperBound(edgeType: String, attributeType: String, upperBound: Int) extends SingleEdgeRule with DslRule {
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
