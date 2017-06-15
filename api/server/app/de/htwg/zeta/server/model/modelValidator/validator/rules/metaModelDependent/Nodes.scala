package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class Nodes(val nodeTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Model is only allowed to contain nodes of types ${nodeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove nodes which are not of types ${nodeTypes.mkString("{", ", ", "}")}."

  override def isValid(node: Node): Option[Boolean] = Some(nodeTypes.contains(node.clazz.name))

  override val dslStatement: String = s"""Nodes areOfTypes ${Util.stringSeqToSeqString(nodeTypes)}"""
}

object Nodes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Seq(new Nodes(metaModel.classes.values.filter(!_.abstractness).map(_
    .name).toSeq))
}
