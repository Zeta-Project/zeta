package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesValuesNoNullValues extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The attribute value of an attribute inside a node is Null."
  override val possibleFix: String = "Replace the Null value with a valid Attribute Value object."

  override def check(model: Model): Boolean = !model.nodes.flatMap(_.attributeValues.values).contains(null) // scalastyle:ignore null
}
