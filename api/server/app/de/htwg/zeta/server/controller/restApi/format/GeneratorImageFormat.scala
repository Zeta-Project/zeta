package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.GeneratorImage
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object GeneratorImageFormat extends Writes[GeneratorImage] {

  override def writes(o: GeneratorImage): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "dockerImage" -> o.dockerImage
    )
  }
}
