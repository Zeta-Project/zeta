package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesNoAttributes(val edgeType: String) extends SingleEdgeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must not have attributes."
  override val possibleFix: String = s"Remote all attributes from edges of type $edgeType."

  override def isValid(edge: EdgeInstance): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: EdgeInstance): Boolean = edge.attributeValues.values.isEmpty

  override val dslStatement: String = s"""Edges ofType "$edgeType" haveNoAttributes ()"""
}

object EdgesNoAttributes extends GeneratorRule {
  override def generateFor(metaModel: Concept): Seq[DslRule] = metaModel.referenceMap.values
    .filter(_.attributes.isEmpty)
    .map(ref => new EdgesNoAttributes(ref.name)).toSeq
}
