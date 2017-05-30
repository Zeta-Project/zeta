package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D07_EdgeTargetsUpperBound(edgeType: String, targetType: String, upperBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have a maximum of $upperBound target nodes of type $targetType."
  override val possibleFix: String = s"Remove target nodes of type $targetType from edges of type $edgeType until there are a maximum of $upperBound target nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = if (upperBound == -1) true else edge.target.find(_.`type`.name == targetType) match {
    case Some(target) => target.nodes.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Targets ofEdges "$edgeType" toNodes "$targetType" haveUpperBound $upperBound"""
}

object D07_EdgeTargetsUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.target.map(target => new D07_EdgeTargetsUpperBound(currentReference.name, target.mType.name, target.upperBound))
    }
}
