package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D21_EdgesNoTargets(edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have target nodes."
  override val possibleFix: String = s"Remove all target nodes from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.target.flatten(_.nodes).isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoTargets ()"""
}

object D21_EdgesNoTargets extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .filter(_.target.isEmpty)
    .map(ref => new D21_EdgesNoTargets(ref.name))
}
