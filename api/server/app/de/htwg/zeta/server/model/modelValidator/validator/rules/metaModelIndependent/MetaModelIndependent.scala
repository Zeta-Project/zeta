package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

object MetaModelIndependent {

  val rules = Seq(
    new EdgesAttributesNamesNotEmpty,
    new NodesAttributesNamesNotEmpty,
    new EdgesAttributeSet,
    new NodesAttributeSet,
    new ElementsIdsNotEmpty,
    new ElementsIdsUnique
  )

}
