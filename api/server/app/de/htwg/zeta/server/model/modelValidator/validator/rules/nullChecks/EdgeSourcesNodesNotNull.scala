package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeSourcesNodesNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The sources nodes list inside an edge is Null."
  override val possibleFix: String = "Replace the Null value by an empty list."

  override def check(model: Model): Boolean = !model.edges.flatMap(_.sourceNodeName).map(_.nodeNames).contains(null) // scalastyle:ignore null
}
