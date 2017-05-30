package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D01_Edges(edgeTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Model is only allowed to contain edges of types ${edgeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove edges which are not of types ${edgeTypes.mkString("{", ", ", "}")}."

  override def isValid(edge: Edge): Option[Boolean] = Some(edgeTypes.contains(edge.`type`.name))

  override val dslStatement: String = s"""Edges areOfTypes ${Util.stringSeqToSeqString(edgeTypes)}"""
}

object D01_Edges extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Seq(new D01_Edges(Util.getReferences(metaModel).map(_.name)))
}