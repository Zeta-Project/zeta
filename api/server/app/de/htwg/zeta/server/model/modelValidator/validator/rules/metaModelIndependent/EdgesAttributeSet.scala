package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesAttributeSet extends SingleEdgeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "In the attribute set, every attribute name must exist only once."
  override val possibleFix: String = "Merge multiple attribute definitions."

  override def isValid(edge: Edge): Option[Boolean] = Some(edge.attributes.map(_.name).distinct.size == edge.attributes.size)
}
