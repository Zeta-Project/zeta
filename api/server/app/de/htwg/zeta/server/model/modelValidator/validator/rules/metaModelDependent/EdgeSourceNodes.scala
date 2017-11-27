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
class EdgeSourceNodes(val edgeType: String, val sourceTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType are only allowed to have source nodes of types ${sourceTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all source nodes that are not of types ${sourceTypes.mkString("{", ", ", "}")} from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = Seq(edge.sourceNodeName).foldLeft(true) { (acc, sourceName) =>
    if (sourceTypes.contains(sourceName)) acc else false
  }

  override val dslStatement: String = s"""Sources ofEdges "$edgeType" areOfTypes ${Util.stringSeqToSeqString(sourceTypes)}"""
}

object EdgeSourceNodes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      if (currentReference.sourceClassName.isEmpty) {
        acc
      } else {
        acc :+ new EdgeSourceNodes(currentReference.name, Seq(currentReference.sourceClassName))
      }
    }
}
