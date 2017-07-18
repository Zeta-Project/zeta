package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.Generator
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object GeneratorFormat extends Writes[Generator] {

  override def writes(o: Generator): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "imageId" -> o.imageId
    )
  }
}
