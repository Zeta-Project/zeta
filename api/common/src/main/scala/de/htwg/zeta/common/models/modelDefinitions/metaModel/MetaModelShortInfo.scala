package de.htwg.zeta.common.models.modelDefinitions.metaModel

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.__
import play.api.libs.json.Writes


/**
 * Represents concise information on a metamodel, used for REST-API overview uri
 *
 * @param id    the id of the metamodel
 * @param name  the name of the metamodel
 * @param links optional attribute for HATEOAS links, only used when serving to clients
 */
case class MetaModelShortInfo(id: UUID, name: String, links: Option[Seq[HLink]] = None)

object MetaModelShortInfo {

  implicit val reads: Reads[MetaModelShortInfo] = (
    (__ \ "id").read[UUID] and
      (__ \ "metaModel" \ "name").read[String] and
      (__ \ "links").readNullable[Seq[HLink]]
    ) (MetaModelShortInfo.apply _)

  implicit val writes: Writes[MetaModelShortInfo] = Json.writes[MetaModelShortInfo]
}
