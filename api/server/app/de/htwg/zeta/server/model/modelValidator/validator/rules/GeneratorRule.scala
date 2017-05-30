package de.htwg.zeta.server.model.modelValidator.validator.rules

import models.modelDefinitions.metaModel.MetaModel

trait GeneratorRule {
  def generateFor(metaModel: MetaModel): Seq[DslRule]
}
