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
class Edges(val edgeTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Model is only allowed to contain edges of types ${edgeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove edges which are not of types ${edgeTypes.mkString("{", ", ", "}")}."

  override def isValid(edge: Edge): Option[Boolean] = Some(edgeTypes.contains(edge.`type`.name))

  override val dslStatement: String = s"""Edges areOfTypes ${Util.stringSeqToSeqString(edgeTypes)}"""
}

object Edges extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Seq(new Edges(Util.getReferences(metaModel).map(_.name)))
}
