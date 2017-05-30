package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D08_EdgeTargetsLowerBound(edgeType: String, targetType: String, lowerBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have at least $lowerBound target nodes of type $targetType."
  override val possibleFix: String = s"Add target nodes of type $targetType to edge of type $edgeType until there are at least $lowerBound target nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.target.find(_.`type`.name == targetType) match {
    case Some(target) => target.nodes.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Targets ofEdges "$edgeType" toNodes "$targetType" haveLowerBound $lowerBound"""
}

object D08_EdgeTargetsLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.target.map(target => new D08_EdgeTargetsLowerBound(currentReference.name, target.mType.name, target.lowerBound))
    }
}
