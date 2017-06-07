package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoInputs
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoOutputs

class NodesOfType(nodeType: String) {

  def haveNoAttributes(): NodesNoAttributes = new NodesNoAttributes(nodeType)

  def haveNoInputs(): NodesNoInputs = new NodesNoInputs(nodeType)

  def haveNoOutputs(): NodesNoOutputs = new NodesNoOutputs(nodeType)

}
