package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeTargetsLowerBound(val edgeType: String, val targetType: String, val lowerBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have at least $lowerBound target nodes of type $targetType."
  override val possibleFix: String = s"Add target nodes of type $targetType to edge of type $edgeType until there are at least $lowerBound target nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.target.find(_.className == targetType) match {
    case Some(target) => target.nodeNames.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Targets ofEdges "$edgeType" toNodes "$targetType" haveLowerBound $lowerBound"""
}

object EdgeTargetsLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.target.map(target => new EdgeTargetsLowerBound(currentReference.name, target.className, target.lowerBound))
    }
}
