package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D02_EdgeAttributes(edgeType: String, attributeTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType are only allowed to have attributes of types ${attributeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all attributes that are not of types ${attributeTypes.mkString("{", ", ", "}")} from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.map(_.name).foldLeft(true) { (acc, attributeName) =>
    if (attributeTypes.contains(attributeName)) acc else false
  }

  override val dslStatement: String = s"""Attributes inEdges "$edgeType" areOfTypes ${Util.stringSeqToSeqString(attributeTypes)}"""
}

object D02_EdgeAttributes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      if (currentReference.attributes.isEmpty) acc
      else acc :+ new D02_EdgeAttributes(currentReference.name, currentReference.attributes.map(_.name))
    }
}
