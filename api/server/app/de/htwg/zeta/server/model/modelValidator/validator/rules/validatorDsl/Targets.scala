package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Targets {

  def ofEdges(edgeType: String) = new TargetsOfEdges(edgeType)

}
