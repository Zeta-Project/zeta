package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * A general rule for checking the model against.
 */
trait ModelRule extends Rule {

  /**
   * Checks the model against the rule.
   *
   * @param model The model.
   * @return True if the model is valid, false otherwise.
   */
  def check(model: GraphicalDslInstance): Boolean
}
