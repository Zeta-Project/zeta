package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference

object TestUtil {

  def classesToMetaModel(elements: Seq[MClass]): Concept = {
    Concept(
      name = "",
      classes = elements,
      references = Seq.empty,
      enums = Seq.empty,
      methods = Seq.empty,
      attributes = Seq.empty,
      uiState = ""
    )
  }

  def referencesToMetaModel(elements: Seq[MReference]): Concept = {
    Concept(
      name = "",
      classes = Seq.empty,
      references = elements,
      enums = Seq.empty,
      methods = Seq.empty,
      attributes = Seq.empty,
      uiState = ""
    )
  }

}
