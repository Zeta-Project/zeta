package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.Filter
import grizzled.slf4j.Logging
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/**
 * Parse JsValue to Filter and Filter to JsValue
 */
class FilterFormat(
    sId: String = "id",
    sName: String = "name",
    sDescription: String = "description",
    sInstanceIds: String = "instanceIds",
    sFiles: String = "files",
    sDeleted: String = "deleted"
) extends OFormat[Filter] with Logging {

  override def writes(filer: Filter): JsObject = {
    Json.obj(
      sId -> filer.id.toString,
      sName -> filer.name,
      sDescription -> filer.description,
      sInstanceIds -> filer.instanceIds,
      sFiles -> Writes.map[String].writes(filer.files.map(e => (e._1.toString, e._2))),
      sDeleted -> filer.deleted
    )
  }

  override def reads(json: JsValue): JsResult[Filter] = for {
    id <- (json \ sId).validateOpt[UUID]
    name <- (json \ sName).validate[String]
    description <- (json \ sDescription).validate[String]
    instanceIds <- (json \ sInstanceIds).validate[Seq[UUID]]
    files <- (json \ sFiles).validate(Reads.map[String])
    deleted <- (json \ sDeleted).validateOpt[Boolean]
  } yield {
    Filter(
      id = id.getOrElse(UUID.randomUUID()),
      name = name,
      description = description,
      instanceIds = instanceIds,
      files = files.map(e => (UUID.fromString(e._1), e._2)),
      deleted = deleted.getOrElse(false)
    )
  }

}
