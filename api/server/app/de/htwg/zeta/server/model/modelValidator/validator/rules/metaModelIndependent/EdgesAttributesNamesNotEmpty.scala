package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesAttributesNamesNotEmpty extends SingleEdgeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Attribute names of edges attributes must not be empty."
  override val possibleFix: String = "Add a name to every attribute."

  override def isValid(edge: EdgeInstance): Option[Boolean] = Some(!edge.attributeValues.keys.toSeq.contains(""))
}
