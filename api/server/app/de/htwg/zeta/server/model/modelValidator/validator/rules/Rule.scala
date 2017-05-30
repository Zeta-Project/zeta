package de.htwg.zeta.server.model.modelValidator.validator.rules

trait Rule {
  val name: String
  val description: String
  val possibleFix: String
}
