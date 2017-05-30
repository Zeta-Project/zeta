package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Outputs {

  def ofNodes(nodeType: String) = new OutputsOfNodes(nodeType)

}
