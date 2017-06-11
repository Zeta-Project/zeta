package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class NodeOutputEdges(nodeType: String, outputTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Nodes of type $nodeType are only allowed to have output edges of types ${outputTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String =
    s"Remove all output edges that are not of types ${outputTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.outputs.map(_.reference.name).foldLeft(true) { (acc, outputName) =>
    if (outputTypes.contains(outputName)) acc else false
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(outputTypes)}"""
}

object NodeOutputEdges extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.outputs.isEmpty) acc else acc :+ new NodeOutputEdges(currentClass.name, currentClass.outputs.map(_.name))
    }
}
