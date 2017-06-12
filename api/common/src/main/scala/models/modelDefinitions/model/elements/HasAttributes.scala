package models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue

/** A mixin that offers the attributes field */
trait HasAttributes {
  val attributes: Map[String, Seq[AttributeValue]]
}
