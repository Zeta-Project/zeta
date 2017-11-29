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
class EventDrivenTaskFormat(
    sId: String = "id",
    sName: String = "name",
    sGeneratorId: String = "generatorId",
    sFilterId: String = "filterId",
    sEvent: String = "event",
    sDeleted: String = "deleted"
) extends OFormat[EventDrivenTask] {

  override def writes(task: EventDrivenTask): JsObject = Json.obj(
    sId -> task.id.toString,
    sName -> task.name,
    sGeneratorId -> task.generatorId,
    sFilterId -> task.filterId,
    sEvent -> task.event,
    sDeleted -> task.deleted
  )

  override def reads(json: JsValue): JsResult[EventDrivenTask] = for {
    id <- (json \ sId).validateOpt[UUID]
    name <- (json \ sName).validate[String]
    generator <- (json \ sGeneratorId).validate[UUID]
    filter <- (json \ sFilterId).validate[UUID]
    event <- (json \ sEvent).validate[String]
    deleted <- (json \ sDeleted).validateOpt[Boolean]
  } yield {
    EventDrivenTask(id.getOrElse(UUID.randomUUID()), name, generator, filter, event, deleted.getOrElse(false))
  }

}
