package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeSourcesLowerBound(val edgeType: String, val sourceType: String, val lowerBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have at least $lowerBound source nodes of type $sourceType."
  override val possibleFix: String = s"Add source nodes of type $sourceType to edge of type $edgeType until there are at least $lowerBound source nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.sourceNodeName.find(_.className == sourceType) match {
    case Some(source) => source.nodeNames.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Sources ofEdges "$edgeType" toNodes "$sourceType" haveLowerBound $lowerBound"""
}

object EdgeSourcesLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.source.map(source => new EdgeSourcesLowerBound(currentReference.name, source.className, source.lowerBound))
    }
}
