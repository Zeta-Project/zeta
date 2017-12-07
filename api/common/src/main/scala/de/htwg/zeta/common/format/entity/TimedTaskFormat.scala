package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.TimedTask
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

/**
 * Parse JsValue to TimedTask and TimedTask to JsValue
 */
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class TimedTaskFormat(
    sId: String = "id",
    sName: String = "name",
    sGeneratorId: String = "generatorId",
    sFilterId: String = "filterId",
    sInterval: String = "interval",
    sStart: String = "start",
    sDeleted: String = "deleted"
) extends OFormat[TimedTask] {

  override def writes(o: TimedTask): JsObject = Json.obj(
    sId -> o.id.toString,
    sName -> o.name,
    sGeneratorId -> o.generatorId,
    sFilterId -> o.filterId,
    sInterval -> o.interval,
    sStart -> o.start
  )

  override def reads(json: JsValue): JsResult[TimedTask] = for {
    id <- (json \ sId).validateOpt[UUID]
    name <- (json \ sName).validate[String]
    generator <- (json \ sGeneratorId).validate[UUID]
    filter <- (json \ sFilterId).validate[UUID]
    interval <- (json \ sInterval).validate[Int]
    start <- (json \ sStart).validate[String]
  } yield {
    TimedTask(id.getOrElse(UUID.randomUUID()), name, generator, filter, interval, start)
  }

}
