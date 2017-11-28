package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.EventDrivenTask
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

/**
 * Parse JsValue to EventDrivenTask and EventDrivenTask to JsValue
 */
object EventDrivenTaskFormat extends OFormat[EventDrivenTask] {

  val attributeId = "id"
  val attributeName = "name"
  val attributeGenerator = "generatorId"
  val attributeFilter = "filterId"
  val attributeEvent = "event"

  override def writes(o: EventDrivenTask): JsObject = Json.obj(
    attributeId -> o.id.toString,
    attributeName -> o.name,
    attributeGenerator -> o.generatorId,
    attributeFilter -> o.filterId,
    attributeEvent -> o.event
  )

  override def reads(json: JsValue): JsResult[EventDrivenTask] = {
    for {
      id <- (json \ attributeId).validateOpt[UUID]
      name <- (json \ attributeName).validate[String]
      generator <- (json \ attributeGenerator).validate[UUID]
      filter <- (json \ attributeFilter).validate[UUID]
      event <- (json \ attributeEvent).validate[String]
    } yield {
      EventDrivenTask(id.getOrElse(UUID.randomUUID()), name, generator, filter, event)
    }
  }

}
