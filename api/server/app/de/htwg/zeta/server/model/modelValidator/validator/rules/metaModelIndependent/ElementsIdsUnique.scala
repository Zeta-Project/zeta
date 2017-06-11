package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import models.modelDefinitions.model.elements.ModelElement

private[metaModelIndependent] class ElementsIdsUnique extends ElementsRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Element Identifiers must be unique."
  override val possibleFix: String = "Make duplicate identifiers unique."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = elements.groupBy(_.name).values
    .foldLeft(Seq[ModelValidationResult]()) { (acc, elements) =>
      acc ++ elements.map(el => ModelValidationResult(rule = this, valid = elements.size == 1, modelElement = Some(el)))
    }
}
