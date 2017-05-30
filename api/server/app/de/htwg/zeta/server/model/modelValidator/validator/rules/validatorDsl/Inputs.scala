package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Inputs {

  def ofNodes(nodeType: String) = new InputsOfNodes(nodeType)

}
