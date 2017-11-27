package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.EventDrivenTask
import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 * Parse JsValue to EventDrivenTask and EventDrivenTask to JsValue
 */
object EventDrivenTaskFormat extends Format[EventDrivenTask] {

  val attributeName = "name"
  val attributeGenerator = "generatorId"
  val attributeFilter = "filterId"
  val attributeEvent = "event"

  override def writes(o: EventDrivenTask): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      attributeName -> o.name,
      attributeGenerator -> o.generatorId,
      attributeFilter -> o.filterId,
      attributeEvent -> o.event
    )
  }

  override def reads(json: JsValue): JsResult[EventDrivenTask] = {
    for {
      name <- (json \ attributeName).validate[String]
      generator <- (json \ attributeGenerator).validate[UUID]
      filter <- (json \ attributeFilter).validate[UUID]
      event <- (json \ attributeEvent).validate[String]
    } yield {
      EventDrivenTask(UUID.randomUUID(), name, generator, filter, event)
    }
  }
}
