package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import models.modelDefinitions.model.Model

private[nullChecks] class EdgeTargetsNodesNoNullValues extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = ""
  override val possibleFix: String = ""

  override def check(model: Model): Boolean = !model.edges.values.flatMap(_.target).flatMap(_.nodeNames).map(Option(_)).forall(_.isDefined)
}
