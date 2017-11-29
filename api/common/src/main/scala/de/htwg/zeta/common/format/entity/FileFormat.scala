package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue


/**
 * Parse JsValue to File and File to JsValue
 */
class FileFormat(
    sId: String = "id",
    sName: String = "name",
    sContent: String = "content"
) extends Format[File] {

  override def writes(o: File): JsValue = {
    Json.obj(
      sId -> o.id.toString,
      sName -> o.name,
      sContent -> o.content
    )
  }

  override def reads(json: JsValue): JsResult[File] = for {
    id <- (json \ sId).validate[UUID]
    name <- (json \ sName).validate[String]
    content <- (json \ sContent).validate[String]
  } yield {
    File(id, name, content)
  }

}
