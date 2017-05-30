package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D04_EdgeAttributesLowerBound(edgeType: String, attributeType: String, lowerBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have at least $lowerBound attributes of type $attributeType."
  override val possibleFix: String = s"Add attributes of type $attributeType to edges of type $edgeType until there are at least $lowerBound attributes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.find(_.name == attributeType) match {
    case Some(attribute) => attribute.value.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" haveLowerBound $lowerBound"""
}

object D04_EdgeAttributesLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.map(attr => new D04_EdgeAttributesLowerBound(currentReference.name, attr.name, attr.lowerBound))
    }
}
