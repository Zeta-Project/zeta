package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.Node
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributes(val nodeType: String, val attributeTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType are only allowed to have attributes of types ${attributeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all attributes that are not of types ${attributeTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributeValues.keys.foldLeft(true) { (acc, attributeName) =>
    if (attributeTypes.contains(attributeName)) acc else false
  }

  override val dslStatement: String = s"""Attributes inNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(attributeTypes)}"""
}

object NodeAttributes extends GeneratorRule {

  override def generateFor(metaModel: Concept): Seq[DslRule] = Util.inheritAttributes(metaModel.classes)
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.attributes.isEmpty) {
        acc
      } else {
        acc :+ new NodeAttributes(currentClass.name, currentClass.attributes.map(_.name))
      }
    }

}
