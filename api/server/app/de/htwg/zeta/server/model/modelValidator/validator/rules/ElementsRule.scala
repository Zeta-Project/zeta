package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import models.modelDefinitions.model.elements.ModelElement

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Rule checking a sequence of elements.
 */
trait ElementsRule extends Rule {

  /**
   * Check the sequence of elements for validity and return a sequence of model validation results.
   *
   * @param elements The elements.
   * @return The validation results.
   */
  def check(elements: Seq[ModelElement]): Seq[ModelValidationResult]
}
