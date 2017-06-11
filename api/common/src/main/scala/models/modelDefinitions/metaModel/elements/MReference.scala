package models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import play.api.libs.json.Format
import play.api.libs.json.Json

/** The MReference implementation
 *
 * @param name                        the name of the MReference instance
 * @param sourceDeletionDeletesTarget whether source deletion leads to removal of target
 * @param targetDeletionDeletesSource whether target deletion leads to removal of source
 * @param source                      the incoming MClass relationships
 * @param target                      the outgoing MClass relationships
 * @param attributes                  the attributes of the MReference
 */
case class MReference(
    name: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MClassLinkDef],
    target: Seq[MClassLinkDef],
    attributes: Seq[MAttribute]
) extends MObject

object MReference {

  implicit val playJsonFormat: Format[MReference] = Json.format[MReference]

}
