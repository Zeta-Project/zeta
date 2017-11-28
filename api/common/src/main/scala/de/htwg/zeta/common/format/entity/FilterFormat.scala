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
object FilterFormat extends OFormat[Filter] with Logging {

  val attributeId = "id"
  val attributeName = "name"
  val attributeDescription = "description"
  val attributeInstances = "instanceIds"
  val attributeFiles = "files"

  override def writes(o: Filter): JsObject = {
    Json.obj(
      attributeId -> o.id.toString,
      attributeName -> o.name,
      attributeDescription -> o.description,
      attributeInstances -> o.instanceIds,
      attributeFiles -> Writes.map[String].writes(o.files.map(e => (e._1.toString, e._2)))
    )
  }

  override def reads(json: JsValue): JsResult[Filter] = {
    for {
      id <- (json \ attributeId).validateOpt[UUID]
      name <- (json \ FilterFormat.attributeName).validate[String]
      description <- (json \ FilterFormat.attributeDescription).validate[String]
      instances <- (json \ FilterFormat.attributeInstances).validate[Seq[UUID]]
      files <- (json \ FilterFormat.attributeFiles).validate(Reads.map[String])
    } yield {
      Filter(id.getOrElse(UUID.randomUUID()), name, description, instances, files.map(e => (UUID.fromString(e._1), e._2)))
    }
  }

}
