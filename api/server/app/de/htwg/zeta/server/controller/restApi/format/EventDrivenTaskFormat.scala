package de.htwg.zeta.server.controller.restApi.format

import de.htwg.zeta.common.models.entity.EventDrivenTask
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * @author Philipp Daniels
 */
object EventDrivenTaskFormat extends Writes[EventDrivenTask] {

  override def writes(o: EventDrivenTask): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      "name" -> o.name,
      "generatorId" -> o.generatorId,
      "filterId" -> o.filterId,
      "event" -> o.event
    )
  }
}
