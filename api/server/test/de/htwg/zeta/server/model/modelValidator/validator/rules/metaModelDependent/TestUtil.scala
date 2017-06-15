package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference

object TestUtil {

  def classesToMetaModel(elements: Set[MClass]) = {
    MetaModel(
      name = "",
      classes = elements,
      references = Set.empty,
      enums = Set.empty,
      uiState = ""
    )
  }

  def referencesToMetaModel(elements: Set[MReference]) = {
    MetaModel(
      name = "",
      classes = Set.empty,
      references = elements,
      enums = Set.empty,
      uiState = ""
    )
  }

}
