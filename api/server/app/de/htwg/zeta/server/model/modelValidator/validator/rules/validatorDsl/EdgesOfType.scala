package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoSources
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoTargets

class EdgesOfType(edgeType: String) {

  def haveNoAttributes() = new EdgesNoAttributes(edgeType)

  def haveNoSources() = new EdgesNoSources(edgeType)

  def haveNoTargets() = new EdgesNoTargets(edgeType)

}
