package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeTypeNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The reference type inside an edge is Null."
  override val possibleFix: String = "Replace the Null value by a valid reference type."

  override def check(model: GraphicalDslInstance): Boolean = !model.edges.map(_.referenceName).contains(null) // scalastyle:ignore null
}
