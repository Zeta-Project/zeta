package de.htwg.zeta.common.models.modelDefinitions.model

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.__


/**
 * Represents concise information on a model, used for REST-API overview uri
 *
 * @param id          the id of the model
 * @param metaModelId the name of the metamodel
 * @param name        the name of themodel
 * @param links       optional attribute for HATEOAS links, only used when serving to clients
 */
case class ModelShortInfo(id: UUID, metaModelId: UUID, name: String, links: Option[Seq[HLink]] = None)

object ModelShortInfo {

  implicit val reads: Reads[ModelShortInfo] = (
    (__ \ "id").read[UUID] and
      (__ \ "metaModelId").read[UUID] and
      (__ \ "model" \ "name").read[String] and
      (__ \ "links").readNullable[Seq[HLink]]
    ) (ModelShortInfo.apply _)

  implicit val writes: OWrites[ModelShortInfo] = Json.writes[ModelShortInfo]
}
