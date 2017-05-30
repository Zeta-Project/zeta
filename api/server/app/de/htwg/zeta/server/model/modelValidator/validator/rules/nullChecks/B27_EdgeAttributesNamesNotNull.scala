package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import models.modelDefinitions.model.Model

private[nullChecks] class B27_EdgeAttributesNamesNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = ""
  override val possibleFix: String = ""

  override def check(model: Model): Boolean = !Util.getEdges(model).flatMap(_.attributes).map(_.name).contains(null)
}
