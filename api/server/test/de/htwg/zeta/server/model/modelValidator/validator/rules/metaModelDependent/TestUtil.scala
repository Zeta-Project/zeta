package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.MObject

object TestUtil {

  def toMetaModel(elements: Seq[MObject]) = MetaModel(
    name = "",
    elements = elements.map(el => el.name -> el).toMap,
    uiState = ""
  )

}
