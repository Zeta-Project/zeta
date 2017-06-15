package de.htwg.zeta.common.models.modelDefinitions.model.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue

/** A mixin that offers the attributes field */
trait HasAttributes {
  val attributes: Map[String, Set[AttributeValue]]
}
