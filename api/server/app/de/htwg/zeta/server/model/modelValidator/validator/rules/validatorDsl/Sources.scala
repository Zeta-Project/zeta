package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Sources {

  def ofEdges(edgeType: String) = new SourcesOfEdges(edgeType)

}
