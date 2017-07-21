package de.htwg.zeta.server.controller.restApi.format

import java.util.UUID

import de.htwg.zeta.common.models.entity.TimedTask
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue

/**
 * Parse JsValue to TimedTask and TimedTask to JsValue
 */
object TimedTaskFormat extends Format[TimedTask] {

  val attributeName = "name"
  val attributeGenerator = "generatorId"
  val attributeFilter = "filterId"
  val attributeInterval = "interval"
  val attributeStart = "start"

  override def writes(o: TimedTask): JsValue = {
    Json.obj(
      "id" -> o.id.toString,
      attributeName -> o.name,
      attributeGenerator -> o.generatorId,
      attributeFilter -> o.filterId,
      attributeInterval -> o.interval,
      attributeStart -> o.start
    )
  }

  override def reads(json: JsValue): JsResult[TimedTask] = {
    for {
      name <- (json \ attributeName).validate[String]
      generator <- (json \ attributeGenerator).validate[UUID]
      filter <- (json \ attributeFilter).validate[UUID]
      interval <- (json \ attributeInterval).validate[Int]
      start <- (json \ attributeStart).validate[String]
    } yield {
      TimedTask(UUID.randomUUID(), name, generator, filter, interval, start)
    }
  }
}
