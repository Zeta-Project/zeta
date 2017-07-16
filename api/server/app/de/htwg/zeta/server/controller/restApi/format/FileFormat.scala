package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.File
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object FileFormat extends Writes[File] {

  override def writes(o: File): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "content" -> o.content
    )
  }
}
