package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D19_EdgesNoAttributes(edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have attributes."
  override val possibleFix: String = s"Remote all attributes from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.flatten(_.value).isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoAttributes ()"""
}

object D19_EdgesNoAttributes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .filter(_.attributes.isEmpty)
    .map(ref => new D19_EdgesNoAttributes(ref.name))
}
