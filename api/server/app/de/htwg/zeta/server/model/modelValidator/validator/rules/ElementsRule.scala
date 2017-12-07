package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Rule checking a sequence of elements.
 */
trait EdgesRule extends Rule {

  /**
   * Check the sequence of elements for validity and return a sequence of model validation results.
   *
   * @param edges The elements.
   * @return The validation results.
   */
  def check(edges: Seq[Edge]): Seq[ModelValidationResult]
}

trait NodesRule extends Rule {

  /**
   * Check the sequence of elements for validity and return a sequence of model validation results.
   *
   * @param nodes The elements.
   * @return The validation results.
   */
  def check(nodes: Seq[Node]): Seq[ModelValidationResult]
}
