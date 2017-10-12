package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

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
    description: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MClassLinkDef],
    target: Seq[MClassLinkDef],
    attributes: Seq[MAttribute],
    methods: Seq[Method]
) extends MObject with AttributeMap with MethodMap

object MReference {

  def empty(name: String, source: Seq[MClassLinkDef], target: Seq[MClassLinkDef]): MReference =
    MReference(
      name = name,
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = source,
      target = target,
      attributes = Seq.empty,
      methods = Seq.empty
    )

  trait ReferenceMap {

    val references: Seq[MReference]

    /** References mapped to their own names. */
    final val referenceMap: Map[String, MReference] = Option(references).fold(
      Map.empty[String, MReference]
    ) { references =>
      references.filter(Option(_).isDefined).map(reference => (reference.name, reference)).toMap
    }

  }

  def playJsonReads(enums: Seq[MEnum]): Reads[MReference] = new Reads[MReference] {
    override def reads(json: JsValue): JsResult[MReference] = {
      for {
        name <- (json \ "name").validate[String]
        description <- (json \ "description").validate[String]
        sourceDeletionDeletesTarget <- (json \ "sourceDeletionDeletesTarget").validate[Boolean]
        targetDeletionDeletesSource <- (json \ "targetDeletionDeletesSource").validate[Boolean]
        source <- (json \ "source").validate(Reads.list[MClassLinkDef])
        target <- (json \ "target").validate(Reads.list[MClassLinkDef])
        attributes <- (json \ "attributes").validate(Reads.list(MAttribute.playJsonReads(enums)))
        methods <- (json \ "methods").validate(Reads.list(Method.playJsonReads(enums)))
      } yield {
        MReference(
          name = name,
          description = description,
          sourceDeletionDeletesTarget = sourceDeletionDeletesTarget,
          targetDeletionDeletesSource = targetDeletionDeletesSource,
          source = source,
          target = target,
          attributes = attributes,
          methods = methods
        )
      }
    }
  }

  implicit val playJsonWrites: Writes[MReference] = Json.writes[MReference]

}
