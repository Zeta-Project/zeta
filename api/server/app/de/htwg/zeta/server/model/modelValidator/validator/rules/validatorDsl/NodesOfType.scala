package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D19_NodesNoAttributes, D22_NodesNoInputs, D23_NodesNoOutputs}

class NodesOfType(nodeType: String) {

  def haveNoAttributes() = new D19_NodesNoAttributes(nodeType)

  def haveNoInputs() = new D22_NodesNoInputs(nodeType)

  def haveNoOutputs() = new D23_NodesNoOutputs(nodeType)

}
