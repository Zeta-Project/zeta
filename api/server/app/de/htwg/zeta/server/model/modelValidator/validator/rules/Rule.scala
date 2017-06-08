package de.htwg.zeta.server.model.modelValidator.validator.rules

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Base class of every rule.
 */
trait Rule {

  /**
   * Name of the rule.
   */
  val name: String

  /**
   * Description of the rule.
   */
  val description: String

  /**
   * Textual description of what could be changed to validate the model it it is invalid.
   */
  val possibleFix: String
}
