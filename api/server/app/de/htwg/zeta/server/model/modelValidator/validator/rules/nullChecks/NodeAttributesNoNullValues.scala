package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesNoNullValues extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "A value of the attribute list inside a node is Null."
  override val possibleFix: String = "Remove the Null value."

  override def check(model: Model): Boolean = !model.nodes.flatMap(_.attributes).map(_._2).contains(null) // scalastyle:ignore null
}
