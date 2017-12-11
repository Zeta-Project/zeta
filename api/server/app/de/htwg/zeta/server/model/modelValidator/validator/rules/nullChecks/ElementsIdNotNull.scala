package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class ElementsIdNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The Id of an element is Null."
  override val possibleFix: String = "Replace the Null value by a valid Id."

  override def check(model: GraphicalDslInstance): Boolean =
    !model.nodes.map(_.name).contains(null) && !model.nodes.map(_.name).contains(null) // scalastyle:ignore null
}
