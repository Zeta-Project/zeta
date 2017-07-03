package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Collection of all the rules the model can be validated against which are not dependent on the meta model.
 */
object MetaModelIndependent {

  /**
   * The meta model independent rules.
   */
  val rules = Seq(
    new EdgesAttributesNamesNotEmpty,
    new NodesAttributesNamesNotEmpty,
    new ElementsIdsNotEmpty,
    new ElementsIdsUnique
  )

}
