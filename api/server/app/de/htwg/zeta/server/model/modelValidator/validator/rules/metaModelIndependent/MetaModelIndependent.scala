package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

object MetaModelIndependent {

  val rules = Seq(
    new I01_EdgesAttributesNamesNotEmpty,
    new I01_NodesAttributesNamesNotEmpty,
    new I02_EdgesAttributeSet,
    new I02_NodesAttributeSet,
    new I03_ElementsIdsNotEmpty,
    new I04_ElementsIdsUnique
  )

}
