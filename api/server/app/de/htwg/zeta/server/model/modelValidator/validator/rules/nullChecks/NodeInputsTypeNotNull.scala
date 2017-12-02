package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeInputsTypeNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The input type inside a node is Null."
  override val possibleFix: String = "Replace Null value by a valid input type."

  override def check(model: GraphicalDslInstance): Boolean = !model.nodes.map(_.inputEdgeNames).contains(null) // scalastyle:ignore null
}
