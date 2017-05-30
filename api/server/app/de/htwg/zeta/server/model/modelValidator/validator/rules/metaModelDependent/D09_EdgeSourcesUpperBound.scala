package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D09_EdgeSourcesUpperBound(edgeType: String, sourceType: String, upperBound: Int) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType must have a maximum of $upperBound source nodes of type $sourceType."
  override val possibleFix: String = s"Remove source nodes of type $sourceType from edges of type $edgeType until there are a maximum of $upperBound source nodes."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = if (upperBound == -1) true else edge.source.find(_.`type`.name == sourceType) match {
    case Some(source) => source.nodes.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Sources ofEdges "$edgeType" toNodes "$sourceType" haveUpperBound $upperBound"""
}

object D09_EdgeSourcesUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.source.map(source => new D09_EdgeSourcesUpperBound(currentReference.name, source.mType.name, source.upperBound))
    }
}
