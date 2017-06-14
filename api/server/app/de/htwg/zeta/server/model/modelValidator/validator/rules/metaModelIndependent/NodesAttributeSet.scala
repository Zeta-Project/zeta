package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodesAttributeSet extends SingleNodeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "In the attribute set, every attribute name must exist only once."
  override val possibleFix: String = "Merge multiple attribute definitions."

  override def isValid(node: Node): Option[Boolean] = Some(node.attributes.map(_.name).distinct.size == node.attributes.size)
}
