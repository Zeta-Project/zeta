package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class EdgesNoSources(edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have source nodes."
  override val possibleFix: String = s"Remove all source nodes from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.source.flatten(_.nodeNames).isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoSources ()"""
}

object EdgesNoSources extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .filter(_.source.isEmpty)
    .map(ref => new EdgesNoSources(ref.name)).toSeq
}
