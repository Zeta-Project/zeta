package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class EdgeAttributes(edgeType: String, attributeTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType are only allowed to have attributes of types ${Util.stringSeqToSeqString(attributeTypes)}."
  override val possibleFix: String = s"Remove all attributes that are not of types ${Util.stringSeqToSeqString(attributeTypes)} from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.keys.foldLeft(true) { (acc, attributeName) =>
    if (attributeTypes.contains(attributeName)) acc else false
  }

  override val dslStatement: String = s"""Attributes inEdges "$edgeType" areOfTypes ${Util.stringSeqToSeqString(attributeTypes)}"""
}

object EdgeAttributes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      if (currentReference.attributes.isEmpty) {
        acc
      } else {
        acc :+ new EdgeAttributes(currentReference.name, currentReference.attributes.map(_.name))
      }
    }
}
