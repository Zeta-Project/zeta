package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeTargetsTypeNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The target type inside an edge is Null."
  override val possibleFix: String = "Replace Null value by a valid target type."

  override def check(model: Model): Boolean = !model.edges.map(_.targetNodeName).contains(null) // scalastyle:ignore null
}
