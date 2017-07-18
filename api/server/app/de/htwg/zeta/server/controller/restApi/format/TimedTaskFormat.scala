package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.TimedTask
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object TimedTaskFormat extends Writes[TimedTask] {

  override def writes(o: TimedTask): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "generatorId" -> o.generatorId,
      "filterId" -> o.filterId,
      "interval" -> o.interval,
      "start" -> o.start
    )
  }
}
