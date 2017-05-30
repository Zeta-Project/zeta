package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.server.model.modelValidator.validator.rules.Rule
import models.modelDefinitions.model.elements.ModelElement

case class ModelValidationResult(rule: Rule, valid: Boolean, modelElement: Option[ModelElement] = None)
