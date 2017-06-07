package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Sources {

  def ofEdges(edgeType: String): SourcesOfEdges = new SourcesOfEdges(edgeType)

}
