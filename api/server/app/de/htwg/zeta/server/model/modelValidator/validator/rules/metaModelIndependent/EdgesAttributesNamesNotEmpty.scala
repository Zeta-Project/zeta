package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.model.elements.Edge

private[metaModelIndependent] class EdgesAttributesNamesNotEmpty extends SingleEdgeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Attribute names of edges attributes must not be empty."
  override val possibleFix: String = "Add a name to every attribute."

  override def isValid(edge: Edge): Option[Boolean] = Some(!edge.attributes.map(_.name).contains(""))
}
