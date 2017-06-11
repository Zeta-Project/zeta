package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.model.elements.Edge

private[metaModelIndependent] class EdgesAttributeSet extends SingleEdgeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "In the attribute set, every attribute name must exist only once."
  override val possibleFix: String = "Merge multiple attribute definitions."

  override def isValid(edge: Edge): Option[Boolean] = Some(edge.attributes.keys.size == edge.attributes.size)
}
