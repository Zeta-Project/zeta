package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesNamesNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The name of an attribute inside an edge is Null."
  override val possibleFix: String = "Replace the Null value with a valid String."

  override def check(model: GraphicalDslInstance): Boolean = !model.edges.flatMap(_.attributeValues.keys).contains(null) // scalastyle:ignore null

}
