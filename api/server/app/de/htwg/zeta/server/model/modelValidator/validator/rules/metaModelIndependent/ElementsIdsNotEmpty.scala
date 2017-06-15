package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import models.modelDefinitions.model.elements.ModelElement

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class ElementsIdsNotEmpty extends ElementsRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Identifiers of elements must not be empty."
  override val possibleFix: String = "Add an non-empty identifier to every element."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] =
    elements.map(el => ModelValidationResult(rule = this, valid = el.name.nonEmpty, modelElement = Some(el)))
}
