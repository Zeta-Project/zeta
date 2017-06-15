package de.htwg.zeta.server.model.modelValidator.validator.rules

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * A rule that can be called via the validator dsl.
 */
trait DslRule {

  /**
   * Valid scala code: the (dsl) statement that calls the specific rule.
   */
  val dslStatement: String
}
