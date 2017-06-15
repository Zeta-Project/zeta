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
class EdgeTargetNodes(val edgeType: String, val targetTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType are only allowed to have target nodes of types ${targetTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all target nodes that are not of types ${targetTypes.mkString("{", ", ", "}")} from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.target.map(_.clazz.name).foldLeft(true) { (acc, targetName) =>
    if (targetTypes.contains(targetName)) acc else false
  }

  override val dslStatement: String = s"""Targets ofEdges "$edgeType" areOfTypes ${Util.stringSeqToSeqString(targetTypes)}"""
}

object EdgeTargetNodes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      if (currentReference.target.isEmpty) {
        acc
      } else {
        acc :+ new EdgeTargetNodes(currentReference.name, currentReference.target.map(_.className))
      }
    }
}
