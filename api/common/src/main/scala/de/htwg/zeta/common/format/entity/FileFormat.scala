package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json


/**
 * Parse JsValue to File and File to JsValue
 */
object FileFormat extends Format[File] {

  val attributeId = "id"
  val attributeName = "name"
  val attributeContent = "content"

  override def writes(o: File): JsValue = {
    Json.obj(
      attributeId -> o.id.toString,
      attributeName -> o.name,
      attributeContent -> o.content
    )
  }

  override def reads(json: JsValue): JsResult[File] = {
    for {
      id <- (json \ attributeId).validate[UUID]
      name <- (json \ attributeName).validate[String]
      content <- (json \ attributeContent).validate[String]
    } yield {
      File(id, name, content)
    }
  }
}
