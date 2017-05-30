package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class D01_Nodes(nodeTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Model is only allowed to contain nodes of types ${nodeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove nodes which are not of types ${nodeTypes.mkString("{", ", ", "}")}."

  override def isValid(node: Node): Option[Boolean] = Some(nodeTypes.contains(node.`type`.name))

  override val dslStatement: String = s"""Nodes areOfTypes ${Util.stringSeqToSeqString(nodeTypes)}"""
}

object D01_Nodes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Seq(new D01_Nodes(Util.getNonAbstractClasses(metaModel).map(_.name)))
}
