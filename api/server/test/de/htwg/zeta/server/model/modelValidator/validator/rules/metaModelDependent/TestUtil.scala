package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference

object TestUtil {

  def classesToMetaModel(elements: Seq[MClass]) = {
    MetaModel(
      name = "",
      classes = elements.map(el => el.name -> el).toMap,
      references = Map.empty,
      enums = Map.empty,
      uiState = ""
    )
  }

  def referencesToMetaModel(elements: Seq[MReference]) = {
    MetaModel(
      name = "",
      classes = Map.empty,
      references = elements.map(el => el.name -> el).toMap,
      enums = Map.empty,
      uiState = ""
    )
  }

}
