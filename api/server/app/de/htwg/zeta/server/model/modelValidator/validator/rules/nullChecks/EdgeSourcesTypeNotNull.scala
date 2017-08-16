package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeSourcesTypeNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The source type inside an edge is Null."
  override val possibleFix: String = "Replace Null value by a valid source type."

  override def check(model: Model): Boolean = !model.edges.flatMap(_.source).map(_.clazz).contains(null) // scalastyle:ignore null
}
