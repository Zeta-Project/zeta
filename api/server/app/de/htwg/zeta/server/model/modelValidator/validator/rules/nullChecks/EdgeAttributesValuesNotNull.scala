package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesValuesNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The attribute value list inside an edge is Null."
  override val possibleFix: String = "Replace the Null value by an empty list."

  override def check(model: Model): Boolean = !model.edges.flatMap(_.attributes.values).contains(null) // scalastyle:ignore null
}
