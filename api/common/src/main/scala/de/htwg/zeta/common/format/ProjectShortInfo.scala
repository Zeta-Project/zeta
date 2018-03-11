package de.htwg.zeta.common.format

import java.util.UUID

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import play.api.libs.json.__


/**
 * Represents concise information on a graphical dsl project, used for REST-API overview uri
 *
 * @param id   the id of the project
 * @param name the name of the project
 */
case class ProjectShortInfo(id: UUID, name: String)

object ProjectShortInfo {

  implicit val reads: Reads[ProjectShortInfo] = (
    (__ \ "id").read[UUID] and
      (__ \ "metaModel" \ "name").read[String]
    ) (ProjectShortInfo.apply _)

  implicit val writes: Writes[ProjectShortInfo] = Json.writes[ProjectShortInfo]
}
