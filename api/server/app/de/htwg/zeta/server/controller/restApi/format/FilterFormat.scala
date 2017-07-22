package de.htwg.zeta.server.controller.restApi.format

import java.util.UUID

import de.htwg.zeta.common.models.entity.Filter
import grizzled.slf4j.Logging
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class FilterFormat extends Reads[Filter] with Writes[Filter] {
  override def reads(json: JsValue): JsResult[Filter] = FilterFormat.reads(json)
  override def writes(o: Filter): JsValue = FilterFormat.writes(o)
}

/**
 * Parse JsValue to Filter and Filter to JsValue
 */
object FilterFormat extends Format[Filter] with Logging {
  val attributeId = "id"
  val attributeName = "name"
  val attributeDescription = "description"
  val attributeInstances = "instanceIds"
  val attributeFiles = "files"

  def apply(): FilterFormat = new FilterFormat()

  override def writes(o: Filter): JsValue = {
    Json.obj(
      attributeId -> o.id.toString,
      attributeName -> o.name,
      attributeDescription -> o.description,
      attributeInstances -> o.instanceIds,
      attributeFiles -> createFiles(o.files)
    )
  }

  private def createFiles(files: Map[UUID, String]): JsObject = {
    val objects = files.map{
      case (key, value) => Json.obj(key.toString -> value)
    }
    objects.reduceLeft((a, b) => a ++ b)
  }

  override def reads(json: JsValue): JsResult[Filter] = {
    for {
      name <- (json \ FilterFormat.attributeName).validate[String]
      description <- (json \ FilterFormat.attributeDescription).validate[String]
      instances <- (json \ FilterFormat.attributeInstances).validate[Seq[UUID]]
      files <- (json \ FilterFormat.attributeFiles).validate[Map[String, String]].map(createFileMap)
    } yield {
      Filter(UUID.randomUUID(), name, description, instances, files)
    }
  }

  private def createFileMap(files: Map[String, String]): Map[UUID, String] = {
    files.map {
      case (key, value) =>
        UUID.fromString(key) -> value
    }
  }
}
