package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D10_EdgeSourcesLowerBound(edgeType: String, sourceType: String, lowerBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have at least $lowerBound source nodes of type $sourceType."
  override val possibleFix: String = s"Add source nodes of type $sourceType to edge of type $edgeType until there are at least $lowerBound source nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.source.find(_.`type`.name == sourceType) match {
    case Some(source) => source.nodes.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Sources ofEdges "$edgeType" toNodes "$sourceType" haveLowerBound $lowerBound"""
}

object D10_EdgeSourcesLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.source.map(source => new D10_EdgeSourcesLowerBound(currentReference.name, source.mType.name, source.lowerBound))
    }
}
