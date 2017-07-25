package de.htwg.zeta.common.models.modelDefinitions.metaModel

import java.util.UUID

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.__


/**
 * Represents concise information on a metamodel, used for REST-API overview uri
 *
 * @param id   the id of the metamodel
 * @param name the name of the metamodel
 */
case class MetaModelShortInfo(id: UUID, name: String)

object MetaModelShortInfo {

  implicit val reads: Reads[MetaModelShortInfo] = (
    (__ \ "id").read[UUID] and
      (__ \ "metaModel" \ "name").read[String]
    ) (MetaModelShortInfo.apply _)

  implicit val writes: Writes[MetaModelShortInfo] = Json.writes[MetaModelShortInfo]
}
