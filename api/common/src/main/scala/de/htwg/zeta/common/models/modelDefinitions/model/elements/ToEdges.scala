package models.modelDefinitions.model.elements

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.Format
import play.api.libs.json.Json

/** Represents outgoing edges of a node
 *
 * @param reference the MReference instance that represents the type of the references
 * @param edgeNames the names of the linked edges
 */
case class ToEdges(reference: MReference, edgeNames: Seq[String]) extends Link

object ToEdges {

  implicit val playJsonToEdgesFormat: Format[ToEdges] = Json.format[ToEdges]

}
