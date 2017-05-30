package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoInputs
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoOutputs

class NodesOfType(nodeType: String) {

  def haveNoAttributes() = new NodesNoAttributes(nodeType)

  def haveNoInputs() = new NodesNoInputs(nodeType)

  def haveNoOutputs() = new NodesNoOutputs(nodeType)

}
