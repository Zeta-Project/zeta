package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import models.modelDefinitions.model.Model

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeTypeNotNull extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = ""
  override val possibleFix: String = ""

  override def check(model: Model): Boolean = !Util.getEdges(model).map(_.`type`).contains(null)
}
