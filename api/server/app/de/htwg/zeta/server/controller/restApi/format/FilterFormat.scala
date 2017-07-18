package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.Filter
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object FilterFormat extends Writes[Filter] {

  override def writes(o: Filter): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "description" -> o.description,
      "instanceIds" -> o.instanceIds
    )
  }
}
