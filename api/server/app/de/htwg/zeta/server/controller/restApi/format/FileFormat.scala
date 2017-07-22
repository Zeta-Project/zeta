package de.htwg.zeta.server.controller.restApi.format

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

class FileFormat extends Reads[File] with Writes[File] {
  override def reads(json: JsValue): JsResult[File] = FileFormat.reads(json)
  override def writes(o: File): JsValue = FileFormat.writes(o)
}

/**
 * Parse JsValue to File and File to JsValue
 */
object FileFormat extends Format[File] {

  val attributeId = "id"
  val attributeName = "name"
  val attributeContent = "content"

  def apply(): FileFormat = new FileFormat()

  override def writes(o: File): JsValue = {
    Json.obj(
      attributeId -> o.id.toString,
      attributeName -> o.name,
      attributeContent -> o.content
    )
  }

  override def reads(json: JsValue): JsResult[File] = {
    for {
      id <- (json \ FileFormat.attributeId).validate[UUID]
      name <- (json \ FileFormat.attributeName).validate[String]
      content <- (json \ FileFormat.attributeContent).validate[String]
    } yield {
      File(id, name, content)
    }
  }
}
