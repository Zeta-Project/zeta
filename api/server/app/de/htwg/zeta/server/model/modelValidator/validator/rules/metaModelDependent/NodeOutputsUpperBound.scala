package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class NodeOutputsUpperBound(val nodeType: String, val outputType: String, val upperBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Nodes of type $nodeType must have a maximum of $upperBound output edges of type $outputType."
  override val possibleFix: String =
    s"Remove output edges of type $outputType from nodes of type $nodeType until there are a maximum of $upperBound output edges."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = if (upperBound == -1) true else node.outputs.find(_.`type`.name == outputType) match {
    case Some(output) => output.edges.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" toEdges "$outputType" haveUpperBound $upperBound"""
}

object NodeOutputsUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.outputs.map(output => new NodeOutputsUpperBound(currentClass.name, output.name, output.upperBound))
    }
}
