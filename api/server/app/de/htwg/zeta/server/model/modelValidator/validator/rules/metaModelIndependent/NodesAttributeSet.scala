package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.model.elements.Node

class NodesAttributeSet extends SingleNodeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "In the attribute set, every attribute name must exist only once."
  override val possibleFix: String = "Merge multiple attribute definitions."

  override def isValid(node: Node): Option[Boolean] = Some(node.attributes.map(_.name).distinct.size == node.attributes.size)
}
