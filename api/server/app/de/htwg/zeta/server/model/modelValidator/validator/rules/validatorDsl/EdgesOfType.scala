package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D19_EdgesNoAttributes, D20_EdgesNoSources, D21_EdgesNoTargets}

class EdgesOfType(edgeType: String) {

  def haveNoAttributes() = new D19_EdgesNoAttributes(edgeType)

  def haveNoSources() = new D20_EdgesNoSources(edgeType)

  def haveNoTargets() = new D21_EdgesNoTargets(edgeType)

}
