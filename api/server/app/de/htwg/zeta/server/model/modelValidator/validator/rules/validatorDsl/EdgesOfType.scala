package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoSources
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoTargets

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgesOfType(edgeType: String) {

  def haveNoAttributes(): EdgesNoAttributes = new EdgesNoAttributes(edgeType)

  def haveNoSources(): EdgesNoSources = new EdgesNoSources(edgeType)

  def haveNoTargets(): EdgesNoTargets = new EdgesNoTargets(edgeType)

}
