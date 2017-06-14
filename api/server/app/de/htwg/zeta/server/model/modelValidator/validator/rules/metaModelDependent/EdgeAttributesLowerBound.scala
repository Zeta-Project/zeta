package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesLowerBound(val edgeType: String, val attributeType: String, val lowerBound: Int) extends SingleEdgeRule with DslRule {
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

object EdgeAttributesLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.map(attr => new EdgeAttributesLowerBound(currentReference.name, attr.name, attr.lowerBound))
    }
}
