package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import models.modelDefinitions.model.elements.ModelElement

class ElementsIdsNotEmpty extends ElementsRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Identifiers of elements must not be empty."
  override val possibleFix: String = "Add an non-empty identifier to every element."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] =
    elements.map(el => ModelValidationResult(rule = this, valid = el.id.nonEmpty, modelElement = Some(el)))
}
