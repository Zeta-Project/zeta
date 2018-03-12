package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
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
  def check(edges: Seq[EdgeInstance]): Seq[ModelValidationResult]
}

trait NodesRule extends Rule {

  /**
   * Check the sequence of elements for validity and return a sequence of model validation results.
   *
   * @param nodes The elements.
   * @return The validation results.
   */
  def check(nodes: Seq[NodeInstance]): Seq[ModelValidationResult]
}
