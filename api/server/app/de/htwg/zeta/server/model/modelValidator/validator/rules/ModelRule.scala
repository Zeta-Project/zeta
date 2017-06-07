package de.htwg.zeta.server.model.modelValidator.validator.rules

import models.modelDefinitions.model.Model

trait ModelRule extends Rule {
  def check(model: Model): Boolean
}
