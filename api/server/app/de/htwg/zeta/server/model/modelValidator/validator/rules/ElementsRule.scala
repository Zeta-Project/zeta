package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import models.modelDefinitions.model.elements.ModelElement

trait ElementsRule extends Rule {
  def check(elements: Seq[ModelElement]): Seq[ModelValidationResult]
}