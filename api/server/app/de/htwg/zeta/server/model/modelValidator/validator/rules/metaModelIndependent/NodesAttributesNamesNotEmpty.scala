package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.model.elements.Node

private[metaModelIndependent] class NodesAttributesNamesNotEmpty extends SingleNodeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Attribute names of nodes attributes must not be empty."
  override val possibleFix: String = "Add name to every attribute."

  override def isValid(node: Node): Option[Boolean] = Some(!node.attributes.keys.toSeq.contains(""))
}
