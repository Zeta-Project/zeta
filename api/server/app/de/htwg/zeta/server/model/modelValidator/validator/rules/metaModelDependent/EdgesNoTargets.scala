package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesNoTargets(val edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have target nodes."
  override val possibleFix: String = s"Remove all target nodes from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.targetNodeName.isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoTargets ()"""
}

object EdgesNoTargets extends GeneratorRule {
  override def generateFor(metaModel: Concept): Seq[DslRule] = metaModel.referenceMap.values
    .filter(_.targetClassName.isEmpty)
    .map(ref => new EdgesNoTargets(ref.name)).toSeq
}
